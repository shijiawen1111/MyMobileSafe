package com.example.mymobilesafe.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.fragment.PwdEnterFragment;
import com.example.mymobilesafe.fragment.PwdManagerFragment;
import com.example.mymobilesafe.utils.Config;
import com.example.mymobilesafe.utils.PreferenceUtils;

public class AppLockSettingActivity extends Activity {

    private TextView mAlsTvTitle;
    private FrameLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock_setting);
        initView();
        initData();
    }

    private void initView() {
        mAlsTvTitle = findViewById(R.id.als_tv_title);
        mContainer = findViewById(R.id.als_container);
    }


    private void initData() {
        // 要么显示密码输入，要么显示密码设置
        // 有密码--》密码输入
        // 没有密码--》密码设置
        String pwd = PreferenceUtils.getString(this, Config.KEY_APP_LOCK_PWD);
        if (TextUtils.isEmpty(pwd)){
            //若是空,则设置密码
            loadPwdManagerFragment();
        }else {
            //若不是空,则输入密码
            loadEnterFragment();
        }
    }

    /**
     * 进入密码设置页面
     */
    private void loadPwdManagerFragment() {
        FragmentManager fm = this.getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.als_container, new PwdManagerFragment());
        transaction.commit();
    }

    /**
     * 进入输入密码的界面
     */
    private void loadEnterFragment() {
        FragmentManager fm = this.getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.als_container, new PwdEnterFragment());
        transaction.commit();
    }
    public void setAppLockTitle(String title){
        mAlsTvTitle.setText(title);
    }
}
