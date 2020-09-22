package com.example.mymobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.bean.CacheBean;
import com.example.mymobilesafe.utils.PackageUtils;

import org.w3c.dom.Text;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JW.S on 2020/9/19 11:56 PM.
 */
public class CacheClearActivity extends Activity implements View.OnClickListener {

    private RelativeLayout mCacheClearScanningContainer;
    private RelativeLayout mCacheClearIconContainer;
    private ImageView mCacheClearIvIcon;
    private ImageView mCacheClearIvScanLine;
    private ProgressBar mCacheClearPb;
    private TextView mCacheClearTvName;
    private TextView mCacheClearTvSize;
    private RelativeLayout mCacheClearScannedContainer;
    private Button mCacheClearBtn;
    private TextView mCacheClearTvDetail;
    private ListView mListView;
    private Button mCacheClerBtnClear;
    private List<CacheBean> mDatas;
    private PackageManager mPm;
    private CacheAdapter mAdapter;
    private CacheScanTask task;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPm = getPackageManager();
        setContentView(R.layout.activity_cache_clear);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        mCacheClearScanningContainer = findViewById(R.id.cc_rl_scanning_container);
        mCacheClearIconContainer = findViewById(R.id.cc_rl_icon_container);
        mCacheClearIvIcon = findViewById(R.id.cc_iv_icon);
        mCacheClearIvScanLine = findViewById(R.id.cc_iv_scan_line);
        mCacheClearPb = findViewById(R.id.cc_progress_bar);
        mCacheClearTvName = findViewById(R.id.cc_tv_name);
        mCacheClearTvSize = findViewById(R.id.cc_tv_cachesize);
        mCacheClearScannedContainer = findViewById(R.id.cc_rl_scanned_container);
        mCacheClearBtn = findViewById(R.id.cc_btn_scan);
        mCacheClearTvDetail = findViewById(R.id.cc_tv_cache_detail);
        mListView = findViewById(R.id.cc_listview);
        mCacheClerBtnClear = findViewById(R.id.cc_btn_clear);
    }
    private void initEvent() {
        mCacheClearBtn.setOnClickListener(this);
        mCacheClerBtnClear.setOnClickListener(this);
    }
    private void initData() {
        //数据加载,假数据加载
        mDatas = new ArrayList<>();
        //for (int i = 0; i < 40; i++) {
        //CacheBean bean = new CacheBean();
        //bean.name = "应用-" + i;
        //bean.cacheSize = 1000;
        //mDatas.add(bean);
        //}
        mAdapter = new CacheAdapter();
        mListView.setAdapter(mAdapter);
        //开始扫描
        startScan();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cc_btn_scan:
                startScan();
                break;
            case R.id.cc_btn_clear:
                clickClear();
                break;
        }
    }

    private class CacheAdapter extends BaseAdapter {
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
            CacheClearItemViewHolder mHolder = null;
            if (convertView == null) {
                //1.加载布局
                //2.新建hoder
                //3.设置标签
                //4.查找控件
                //没有复用
                convertView = View.inflate(CacheClearActivity.this, R.layout.item_cache, null);
                mHolder = new CacheClearItemViewHolder();
                convertView.setTag(mHolder);
                mHolder.mItemCacheIvIcon = convertView.findViewById(R.id.item_cache_iv_icon);
                mHolder.mItemCacheTvName = convertView.findViewById(R.id.item_cache_tv_name);
                mHolder.mItemCacheTvCachesize = convertView.findViewById(R.id.item_cache_tv_cachesize);
                mHolder.mItemCacheIvClear = convertView.findViewById(R.id.item_cache_iv_clear);
            } else {
                //有复用
                mHolder = (CacheClearItemViewHolder) convertView.getTag();
            }
            //设置数据
            final CacheBean bean = mDatas.get(position);
            if (bean.icon == null) {
                mHolder.mItemCacheIvIcon.setImageResource(R.drawable.ic_default);
            } else {
                mHolder.mItemCacheIvIcon.setImageDrawable(bean.icon);
            }
            mHolder.mItemCacheTvName.setText(bean.name);
            mHolder.mItemCacheTvCachesize.setText("缓存大小:" + Formatter.formatFileSize(CacheClearActivity.this, bean.cacheSize));
            if (bean.cacheSize > 0) {
                mHolder.mItemCacheIvClear.setVisibility(View.VISIBLE);
            } else {
                mHolder.mItemCacheIvClear.setVisibility(View.GONE);
            }
            //清理缓存的实现
            mHolder.mItemCacheIvClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse("package:" + bean.packageName));
                    startActivity(intent);
                }
            });
            return convertView;
        }

        private class CacheClearItemViewHolder {
            ImageView mItemCacheIvIcon;
            TextView mItemCacheTvName;
            TextView mItemCacheTvCachesize;
            ImageView mItemCacheIvClear;
        }
    }

    //开始扫描应用程序
    private void startScan() {
        task = new CacheScanTask();
        task.execute();
    }
    //一键清理
    private void clickClear() {
        try {
            //清理所有缓存数据
            Method method = PackageManager.class.getDeclaredMethod("freeStorageAndNotify", Long.TYPE, IPackageDataObserver.class);
            method.setAccessible(true);
            method.invoke(mPm, Long.MAX_VALUE,null);
            //重新扫描
            startScan();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private class CacheScanTask extends AsyncTask<Void, CacheBean, Void> {

        private int max;
        private int progress;
        private int mCacheCount;
        private long mCacheSize;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //隐藏扫描后的,显示正在扫描的
            mCacheClearScannedContainer.setVisibility(View.GONE);
            mCacheClearScanningContainer.setVisibility(View.VISIBLE);
            //扫描的线做动画
            TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 1);
            animation.setDuration(600);
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.REVERSE);
            mCacheClearIvScanLine.setAnimation(animation);
            //初始化缓存个数
            mCacheCount = 0;
            mCacheSize = 0;
            mDatas.clear();
            mAdapter.notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //真实数据(所有的应用)
            List<PackageInfo> packages = mPm.getInstalledPackages(0);
            max = packages.size();
            mCacheClearPb.setProgress(max);
            for (PackageInfo info : packages) {
//                CacheBean bean = new CacheBean();
////                bean.icon = PackageUtils.getAppIcon(CacheClearActivity.this, info);
////                bean.name = PackageUtils.getAppName(CacheClearActivity.this, info);
////                bean.cacheSize = 0;
////                if (bean.cacheSize > 0) {
////                    mCacheCount++;
////                    mCacheSize += bean.cacheSize;
////                }
////                publishProgress(bean);
                try {
                    Method method = PackageManager.class.getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
                    method.invoke(mPm, info.packageName, mStatesObserver);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SystemClock.sleep(50);
            }
            return null;
        }

        IPackageStatsObserver.Stub mStatesObserver = new IPackageStatsObserver.Stub() {
            @Override
            public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) {
                String packageName = pStats.packageName;
                PackageInfo info;
                try {
                    info = mPm.getPackageInfo(packageName, 0);
                    CacheBean bean = new CacheBean();
                    bean.icon = PackageUtils.getAppIcon(CacheClearActivity.this, info);
                    bean.name = PackageUtils.getAppName(CacheClearActivity.this, info);
                    bean.cacheSize = pStats.cacheSize;
                    bean.packageName = packageName;
                    if (bean.cacheSize > 0) {
                        mCacheCount++;
                        mCacheSize += bean.cacheSize;
                    }
                    task.onProgressUpdate(bean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        @Override
        protected void onProgressUpdate(CacheBean... values) {
            super.onProgressUpdate(values);
            final CacheBean bean = values[0];
            //1.逐个添加
            if (bean.cacheSize > 0) {
                //有缓存
                mDatas.add(0, bean);
            } else {
                mDatas.add(bean);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //2.UI逐个更新
                    mAdapter.notifyDataSetChanged();
                    //3.默认滑动到底部
                    mListView.smoothScrollToPosition(mAdapter.getCount());
                    //显示扫描的图标
                    mCacheClearIvIcon.setImageDrawable(bean.icon);
                    mCacheClearTvName.setText(bean.name);
                    mCacheClearTvSize.setText("缓存大小:" + Formatter.formatFileSize(CacheClearActivity.this, bean.cacheSize));
                    mCacheClearPb.setProgress(++progress);
                }
            });
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //隐藏扫描后的,消失扫描中的
            mCacheClearScannedContainer.setVisibility(View.VISIBLE);
            mCacheClearScanningContainer.setVisibility(View.GONE);
            //最后一定要记得关闭之前开启过的动画
            mCacheClearIvScanLine.clearAnimation();
            //设置缓存结果
            String test = "总共有 " + mCacheCount + " 个缓存,大小是 " + Formatter.formatFileSize(CacheClearActivity.this, mCacheSize);
            mCacheClearTvDetail.setText(test);
            //让listview滚动到第一条
            mListView.smoothScrollToPosition(0);
        }
    }
}
