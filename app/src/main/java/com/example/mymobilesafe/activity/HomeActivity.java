package com.example.mymobilesafe.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.bean.HomeBean;
import com.example.mymobilesafe.utils.Config;
import com.example.mymobilesafe.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends Activity implements AdapterView.OnItemClickListener {

    private static final String TAG = "HomeActivity";
    private ImageView mIvLogo;
    private GridView mGvGridView;

    private final int[] ICONS = new int[]{
            R.drawable.sjfd, R.drawable.srlj, R.drawable.rjgj,
            R.drawable.jcgl, R.drawable.lltj, R.drawable.sjsd, R.drawable.hcql, R.drawable.cygj
    };
    private final String[] TITLES = new String[]{
            "手机防盗", "骚扰拦截", "软件管家",
            "进程管理", "流量统计", "手机杀毒", "缓存清理", "常用工具"
    };
    private final String[] DESCS = new String[]{
            "远程定位手机", "全面拦截骚扰", "管理您的软件",
            "管理运行进程", "流量一目了然", "病毒无处藏身", "系统快如火箭", "工具大全"
    };
    private List mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        初始化控件
        initView();
//        让logo进行动画
        doLogoAnimation();
//        给gridView加载数据
        initGridViewData();
//        初始化事件
        initEvent();
    }

    private void initGridViewData() {
//        给list数据初始化
        mDatas = new ArrayList<HomeBean>();
        for (int i = 0; i < ICONS.length; i++) {
            HomeBean bean = new HomeBean();
            bean.icon = ICONS[i];
            bean.title = TITLES[i];
            bean.desc = DESCS[i];
            mDatas.add(bean);
        }
        mGvGridView.setAdapter(new HomeAdapter());
    }

    private void initEvent() {
        mGvGridView.setOnItemClickListener(this);
    }

    private void initView() {
        mIvLogo = findViewById(R.id.home_iv_logo);
        mGvGridView = findViewById(R.id.home_gridview);
    }

    private void doLogoAnimation() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mIvLogo, "rotationY", 0, 90, 180, 270, 360, 180, 270);
        animator.setDuration(3000);//设置动画时长
        animator.setRepeatCount(ObjectAnimator.INFINITE);//设置可重复
        animator.setRepeatMode(ObjectAnimator.REVERSE);//设置重复模式
        animator.start();
    }

    public void onClickSetting(View view) {
        Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
//                    "手机防盗"
            case 0:
                clickSjfd();
                break;
//                    "骚扰拦截"
            case 1:

                break;
//                    "软件管家"
            case 2:

                break;
//                    "进程管理"
            case 3:

                break;
//                    "流量统计"
            case 4:

                break;
//                    "手机杀毒"
            case 5:

                break;
//                    "缓存清理"
            case 6:

                break;
//                    "常用工具"
            case 7:

                break;
            default:
                break;
        }
    }

    /**
     * 点击手机防盗
     */
    private void clickSjfd() {
        //去存储中获得存储的密码
        String pwd = PreferenceUtils.getString(this, Config.KEY_SJFD_PWD);
        //是否设置过密码
        if (!TextUtils.isEmpty(pwd)) {
//            设置过,则显示密码输入的对话框
            showEnterPwdDialog();
        } else {
//            未设置过,则显示密码设置的对话框
        }
    }
    //        设置过,则显示密码输入的对话框
    private void showEnterPwdDialog() {
        Log.d(TAG, "显示密码输入的对话框");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        设置自定义的布局
        View view = View.inflate(this, R.layout.dialog_pwd_enter, null);
        //TODO
    }

    private class HomeAdapter extends BaseAdapter {
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
            if (mDatas != null) {
                return position;
            }
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = View.inflate(HomeActivity.this, R.layout.item_home, null);
            ImageView ivIcon = convertView.findViewById(R.id.item_home_iv_icon);
            TextView tvTitle = convertView.findViewById(R.id.item_home_tv_title);
            TextView tvDesc = convertView.findViewById(R.id.item_home_tv_desc);
//            给控件设置数据
            HomeBean bean = (HomeBean) mDatas.get(position);
            ivIcon.setImageResource(bean.icon);
            tvTitle.setText(bean.title);
            tvDesc.setText(bean.desc);
            return convertView;
        }
    }
}
