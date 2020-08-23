package com.example.mymobilesafe.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.bean.AppInfo;
import com.example.mymobilesafe.business.AppProvider;
import com.example.mymobilesafe.view.ProgressStateView;
import com.mob.MobSDK;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class AppManagerActivity extends Activity implements AdapterView.OnItemClickListener {
    private ProgressStateView mPsvRom;
    private ProgressStateView mPsvSD;
    private ListView mListView;
    private List<AppInfo> mDatas;
    private AppAdapter mAdapter;
    private PackageUninstallReciver mUninstallReciver;
    private LinearLayout mLoadingView;
    private int mSystemCount; //系统程序的个数
    private int mUsedCount;   //应用程序的个数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        initView();
        initEvent();
        initData();
    }

    private void initEvent() {
        mListView.setOnItemClickListener(this);
        // 注册广播接受者,应用卸载
        mUninstallReciver = new PackageUninstallReciver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        //卸载包的行为
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(mUninstallReciver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销广播
        unregisterReceiver(mUninstallReciver);
    }

    private void initView() {
        mPsvRom = findViewById(R.id.am_psv_rom);
        mPsvSD = findViewById(R.id.am_psv_sd);
        mListView = findViewById(R.id.am_listview);
        mLoadingView = findViewById(R.id.include_ll_loading);
    }

    private void initData() {
        // 1. 去获得内部存储的总大小和剩余 (data目录)
        File fileDataDir = Environment.getDataDirectory();
        long romTotalSpace = fileDataDir.getTotalSpace();//总的
        long romFreeSpace = fileDataDir.getFreeSpace();//剩余的
//        long usableSpace = fileDataDir.getUsableSpace();//可用的空间
//        System.out.println("还有:"+usableSpace+"空间可用.");
        long romUsedSpace = romTotalSpace - romFreeSpace;//使用的
        int romProgress = (int) (romUsedSpace * 100f / romTotalSpace + 0.5f);
        // 剩余
        mPsvRom.setRightText(Formatter.formatFileSize(AppManagerActivity.this, romFreeSpace) + "可用");
        mPsvRom.setLeftText(Formatter.formatFileSize(AppManagerActivity.this, romUsedSpace) + "已用");
        mPsvRom.setCurrentProgress(romProgress);

        //获取SD卡的相关信息
        File SDDir = Environment.getExternalStorageDirectory();
        long SDTotalSpace = SDDir.getTotalSpace();//总的
        long SDFreeSpace = SDDir.getFreeSpace();//剩余的
//        long SDUsableSpace = SDDir.getUsableSpace();//可用的空间
//        System.out.println("还有:"+SDUsableSpace+"空间可用.");
        long SDUsedSpace = SDTotalSpace - SDFreeSpace;
        int SDProgress = (int) (SDUsedSpace * 100f / SDTotalSpace + 0.5f);
        mPsvSD.setRightText(Formatter.formatFileSize(AppManagerActivity.this, SDFreeSpace) + "可用");
        mPsvSD.setLeftText(Formatter.formatFileSize(AppManagerActivity.this, SDUsedSpace) + "已用");
        mPsvSD.setCurrentProgress(SDProgress);

        mLoadingView.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //获取所有安装的信息
                mDatas = AppProvider.getAllApps(AppManagerActivity.this);
                Collections.sort(mDatas, new Comparator<AppInfo>() {
                    @Override
                    public int compare(AppInfo lhs, AppInfo rhs) {
                        boolean lhsSystem = lhs.isSystem;       //表示系统应用
                        boolean rhsSystem = rhs.isSystem;      //表示互用程序
                        int lhsInt = lhsSystem ? 1 : 0;         //系统应用所占空间大于1,则返回1;小于1,则返回0.
                        int rhsInt = rhsSystem ? 1 : 0;         //用户应用所占空间大于1,则返回1;小于1,则放回0.
                        return lhsInt - rhsInt;                 //小于0左边在前面.
                    }
                });
                //查找系统程序的个数,用户程序的个数
                for (AppInfo bean : mDatas) {
                    if (bean.isSystem) {
                        mSystemCount++;
                    } else {
                        mUsedCount++;
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLoadingView.setVisibility(View.GONE);
                        //给listview设置adapter
                        mAdapter = new AppAdapter();
                        mListView.setAdapter(mAdapter);
                    }
                });
            }
        }).start();
    }


    private class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mDatas != null) {
                return mDatas.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mDatas != null) {
                return mDatas.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemViewHolder holder;
            if (convertView == null) {
                //没有复用
                //1.加载xml布局
                //2.新建holder
                //3.设置tag
                //4.findviewbyid
                convertView = View.inflate(AppManagerActivity.this, R.layout.item_activity_manager, null);
                holder = new ItemViewHolder();
                convertView.setTag(holder);
                holder.mImageView = convertView.findViewById(R.id.item_am_ic_icon);
                holder.mTvName = convertView.findViewById(R.id.item_am_tv_name);
                holder.mTvInstall = convertView.findViewById(R.id.item_am_tv_install);
                holder.mTvSpace = convertView.findViewById(R.id.item_am_tv_space);
            } else {
                //有复用
                holder = (ItemViewHolder) convertView.getTag();
            }

            //设置数据
            AppInfo bean = mDatas.get(position);
            holder.mImageView.setImageDrawable(bean.icon);
            holder.mTvName.setText(bean.name);
            holder.mTvInstall.setText(bean.isInstallSD ? "SD安装" : "内存安装");
            holder.mTvSpace.setText(Formatter.formatFileSize(AppManagerActivity.this, bean.space));
            return convertView;
        }

        private class ItemViewHolder {
            ImageView mImageView;           //应用图标
            TextView mTvName;               //应用包名
            TextView mTvInstall;            //应用安装位置
            TextView mTvSpace;              //应用占用空间大小
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //View view:点击的条目
        //显示提示框,(卸载,打开,分享,信息)
        //popopWindom使用的步骤.
        //1.去写内容的布局
        //2.创建popupWindom
        View contentView = View.inflate(AppManagerActivity.this, R.layout.popup_app, null);
        final PopupWindow pw = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //额外的设置,让popupWindom只弹出一个
        pw.setFocusable(true);//获得焦点
        pw.setBackgroundDrawable(new ColorDrawable());
        pw.setTouchable(true);

        //动画
        pw.setAnimationStyle(R.style.popAnimation);
        // 3. 显示PopupWindow
        //第一个参数：展示在某某控件的下方
        //第二个参数：表示x 轴的偏移量
        //第三个参数：表示y 轴的偏移量
        System.out.println("view height:" + view.getHeight());
        pw.showAsDropDown(view, 80, -view.getHeight());

        //Popupwindow中一共有四个textview，分别初始化这些控件，然后给他设置点击事件去实现对应的功能
        //一.卸载
        //1.初始化View
        TextView mTvUnInstall = contentView.findViewById(R.id.popup_tv_unintsll);
        TextView mTvOpen = contentView.findViewById(R.id.popup_tv_open);
        TextView mTvShare = contentView.findViewById(R.id.popup_tv_share);
        TextView mTvInfo = contentView.findViewById(R.id.popup_tv_info);
        AppInfo bean = mDatas.get(position);
        final String pakagerName = (bean.pakagerName).trim();
        if (bean.isSystem) {
            //如果是系统应用就隐藏改功能
            mTvUnInstall.setVisibility(View.GONE);
        }
        //点击卸载
        mTvUnInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.DELETE");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:" + pakagerName));
                startActivity(intent);
                //隐藏popupWindom
                pw.dismiss();
            }
        });

        //点击打开
        //由于有些应用程序没有启动项，可能只是一个服务进程或者后台进程则就不能通过api去打开该应用，需要隐藏掉该功能
        PackageManager pm = getPackageManager();
        final Intent intent = pm.getLaunchIntentForPackage(pakagerName);
        if (intent == null) {
            // 没有启动项
            mTvOpen.setVisibility(View.GONE);
        }
        mTvOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开应用
                startActivity(intent);
                //隐藏popopWindom
                pw.dismiss();
            }
        });

        //点击查看详情
        mTvInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:" + pakagerName));
                startActivity(intent);
                pw.dismiss();
            }
        });
        // 点击社会化分享
        mTvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare(pakagerName, pakagerName);
                //隐藏popupWindom
                pw.dismiss();
            }
        });
    }

    private void showShare(String title, String text) {

        //1、初始化MobSDK
        MobSDK.init(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(text);
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("ShareSDK");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(this);
    }

    private class PackageUninstallReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String dataString = intent.getDataString();
            // dataString--> package:包名
            // 包名 --》 mDatas遍历 --》发现有包---》移除---》UI更新
            String packageName = dataString.replace("package:", "");
            ListIterator<AppInfo> iterator = mDatas.listIterator();
            while (iterator.hasNext()) {
                AppInfo bean = iterator.next();
                //对比
                if (bean.pakagerName.equals(packageName)) {
                    //有的==》移除
                    iterator.remove();
                    break;
                }
            }
            // UI更新,adapter更新
            mAdapter.notifyDataSetChanged();
        }
    }
}
