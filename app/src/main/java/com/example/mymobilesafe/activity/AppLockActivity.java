package com.example.mymobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.bean.AppInfo;
import com.example.mymobilesafe.business.AppProvider;
import com.example.mymobilesafe.db.AppLockDao;
import com.example.mymobilesafe.view.SegmentView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JW.S on 2020/9/13 12:45 AM.
 */
public class AppLockActivity extends Activity {

    private SegmentView mSegmentView;
    private TextView mTvTitle;
    private ListView mLockListView;
    private ListView mUnLockListView;
    private List<AppInfo> mLockDatas;
    private List<AppInfo> mUnLockDatas;
    private AppLockDao mDao;
    private boolean isAnimation;
    private AppLockAdapter mUnLockAdapter;
    private AppLockAdapter mLockAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        mDao = new AppLockDao(AppLockActivity.this);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        mSegmentView = findViewById(R.id.al_segmentview);
        mTvTitle = findViewById(R.id.al_tv_title);
        mLockListView = findViewById(R.id.al_listview_lock);
        mUnLockListView = findViewById(R.id.al_listview_unlock);
    }

    private void initData() {
        //         加载list数据 --假数据展示
        //        mLockDatas = new ArrayList<>();
        //        for (int i = 0; i < 30; i++) {
        //            AppInfo bean = new AppInfo();
        //            bean.name = "加锁的程序-" + i;
        //            mLockDatas.add(bean);
        //        }
        //        mUnLockDatas = new ArrayList<>();
        //        for (int i = 0; i < 30; i++) {
        //            AppInfo bean = new AppInfo();
        //            bean.name = "未加锁的程序-" + i;
        //            mUnLockDatas.add(bean);
        //        }
        //真实数据的加载
        mLockDatas = new ArrayList<>();
        mUnLockDatas = new ArrayList<>();
        List<AppInfo> allsLuchApps = AppProvider.getAllsLuchApps(AppLockActivity.this);
        for (AppInfo bean : allsLuchApps) {
            String pakagerName = bean.pakagerName;
            if (mDao.findIsLock(pakagerName)) {
                //加锁
                mLockDatas.add(bean);
            } else {
                //没有加锁
                mUnLockDatas.add(bean);
            }
        }
        //给未加锁的设置adapter
        mUnLockAdapter = new AppLockAdapter(false);
        mUnLockListView.setAdapter(mUnLockAdapter);
        //给加锁的设置adapter
        mLockAdapter = new AppLockAdapter(true);
        mLockListView.setAdapter(mLockAdapter);
    }

    private void initEvent() {
        // 分段控件左右选中时，会影响下面布局的显示
        mSegmentView.setOnCheckListener(new SegmentView.OnCheckListener() {
            @Override
            public void onCheck(View view, boolean leftSelected) {
                Toast.makeText(AppLockActivity.this, leftSelected ? "左侧选中" : "右侧选中", Toast.LENGTH_SHORT).show();
                if (leftSelected) {
                    //显示未加锁的
                    mLockListView.setVisibility(View.GONE);
                    mUnLockListView.setVisibility(View.VISIBLE);
                } else {
                    //显示已经加锁的
                    mLockListView.setVisibility(View.VISIBLE);
                    mUnLockListView.setVisibility(View.GONE);
                }
            }
        });
    }

    private class AppLockAdapter extends BaseAdapter {
        private boolean locked;

        public AppLockAdapter(boolean locked) {
            this.locked = locked;
        }

        @Override
        public int getCount() {
            if (locked) {
                //已经上锁的
                if (mLockDatas != null) {
                    mTvTitle.setText("加锁程序(" + mLockDatas.size() + ")");
                    return mLockDatas.size();
                }
                mTvTitle.setText("加锁程序(0)");
            } else {
                //未上锁饿
                if (mUnLockDatas != null) {
                    mTvTitle.setText("未加锁程序(" + mUnLockDatas.size() + ")");
                    return mUnLockDatas.size();
                }
                mTvTitle.setText("未加锁程序(0)");
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (locked) {
                //已上锁的
                if (mLockDatas != null) {
                    return mLockDatas.get(position);
                }
            } else {
                //未上锁的
                if (mUnLockDatas != null) {
                    return mUnLockDatas.get(position);
                }
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                //没有复用
                //1.加载布局
                //2.新建holder
                //3.设置标签
                //4.查找控件
                convertView = View.inflate(AppLockActivity.this, R.layout.item_app_lock, null);
                holder = new ViewHolder();
                convertView.setTag(holder);
                holder.mItemIvIcon = convertView.findViewById(R.id.item_al_iv_icon);
                holder.mItemTvName = convertView.findViewById(R.id.item_al_tv_name);
                holder.mItemIvLock = convertView.findViewById(R.id.item_al_iv_lock);
            } else {
                //有复用
                holder = (ViewHolder) convertView.getTag();
            }
            //设置数据
            AppInfo bean = null;
            if (locked) {
                bean = mLockDatas.get(position);
            } else {
                bean = mUnLockDatas.get(position);
            }
            if (bean.icon == null) {
                holder.mItemIvIcon.setImageResource(R.drawable.ic_default);
            } else {
                holder.mItemIvIcon.setImageDrawable(bean.icon);
            }
            holder.mItemTvName.setText(bean.name);
            final AppInfo app = bean;
            final View contentView = convertView;
            if (locked) {
                holder.mItemIvLock.setImageResource(R.drawable.btn_unlock_selector);
                //设置解锁点击事件
                holder.mItemIvLock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickRemoveLock(app, contentView);
                    }
                });
            } else {
                holder.mItemIvLock.setImageResource(R.drawable.btn_lock_selector);
                //设置加锁点击事件
                holder.mItemIvLock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickAddLock(app, contentView);
                    }
                });
            }
            return convertView;
        }

        public class ViewHolder {
            ImageView mItemIvIcon;
            TextView mItemTvName;
            ImageView mItemIvLock;
        }
    }

    private void clickAddLock(final AppInfo app, View contentView) {
        // 当前全局中有动画
        if (isAnimation) {
            return;
        }

        // 动画的移动整个条目
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT,
                1, Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0);
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                isAnimation = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimation = false;
                // 将条目添加到数据库
                String packageName = app.pakagerName;
                boolean add = mDao.add(packageName);
                if (add) {
                    // 移除数据从 未加锁的数据集合中
                    mUnLockDatas.remove(app);
                    // 添加数据到 加锁的集合中
                    mLockDatas.add(app);

                    // UI更新
                    mUnLockAdapter.notifyDataSetChanged();
                }
            }
        });
        contentView.startAnimation(animation);
    }

    private void clickRemoveLock(final AppInfo app, View contentView) {
        // 当前全局中有动画
        if (isAnimation) {
            return;
        }

        // 动画的移动整个条目
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT,
                -1, Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0);
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                isAnimation = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimation = false;
                // 将条目从数据库中移除
                String packageName = app.pakagerName;
                boolean delete = mDao.delete(packageName);
                if (delete) {
                    // 从 已加锁的集合中移除
                    mLockDatas.remove(app);
                    // 添加到未加锁的集合中
                    mUnLockDatas.add(app);

                    // UI更新
                    mLockAdapter.notifyDataSetChanged();
                }
            }
        });
        contentView.startAnimation(animation);
    }
}
