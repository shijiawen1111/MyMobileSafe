package com.example.mymobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.service.AutoCleanService;
import com.example.mymobilesafe.service.CallSmsSafeService;
import com.example.mymobilesafe.service.NumberAddressService;
import com.example.mymobilesafe.utils.Config;
import com.example.mymobilesafe.utils.PreferenceUtils;
import com.example.mymobilesafe.utils.ServiceStateUtils;
import com.example.mymobilesafe.view.AddressStyleDialog;
import com.example.mymobilesafe.view.SettingItemView;

public class SettingActivity extends Activity implements View.OnClickListener {

    private SettingItemView mSivAutoUpdate;
    private SettingItemView mSivAutoCallSmsSafe;
    private SettingItemView mSivAutoClean;
    private SettingItemView mSivNumberAddress;
    private SettingItemView mSivNumberAddressStyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
//        初始化控件
        initView();
//        初始化事件
        initEvent();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mSivAutoUpdate = findViewById(R.id.setting_siv_autoupdate);
        mSivAutoCallSmsSafe = findViewById(R.id.setting_siv_callsmssafe);
        mSivAutoClean = findViewById(R.id.setting_siv_autoclean);
        mSivNumberAddress = findViewById(R.id.setting_siv_number_address);
        mSivNumberAddressStyle = findViewById(R.id.setting_siv_address_style);
//        校验自动更新状态
        mSivAutoUpdate.setToggleState(PreferenceUtils.getBoolean(SettingActivity.this, Config.KEY_AUTO_UPDATE, true
        ));
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        mSivAutoUpdate.setOnClickListener(this);
        mSivAutoCallSmsSafe.setOnClickListener(this);
        mSivAutoClean.setOnClickListener(this);
        mSivNumberAddress.setOnClickListener(this);
        mSivNumberAddressStyle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//                自动更新设置
            case R.id.setting_siv_autoupdate:
                clickAutoUpdate();
                break;
//                骚扰拦截设置
            case R.id.setting_siv_callsmssafe:
                if (v == mSivAutoCallSmsSafe) {
                    clickCallSmsSafe();
                } else if (v == mSivAutoUpdate) {
                    clickAutoUpdate();
                }
                break;
//                自动清除缓存设置
            case R.id.setting_siv_autoclean:
                clickAutoClean();
                break;
//                归属地显示设置
            case R.id.setting_siv_number_address:
                clickNumberAddress();
                break;
//                归属地显示风格设置
            case R.id.setting_siv_address_style:
                clickAddressStyle();
                break;
            default:
                break;
        }
    }

    private void clickAutoUpdate() {
        // 1.获取已经保存的数据
        boolean flag = PreferenceUtils.getBoolean(SettingActivity.this, Config.KEY_AUTO_UPDATE, true);
        // 如果已经设置了自动更新，点击时，要关闭,否则相反
//        if (flag) {
////            close
//            mSivAutoUpdate.setToggleState(false);
//        } else {
////            open
//            mSivAutoUpdate.setToggleState(true);
//        }
        mSivAutoUpdate.setToggleState(!flag);

        // 2.设置数据存储的改变
//        if (flag) {
////            false
//            PreferenceUtils.setBoolean(SettingActivity.this, Config.KEY_AUTO_UPDATE, false);
//        } else {
////            true
//            PreferenceUtils.setBoolean(SettingActivity.this, Config.KEY_AUTO_UPDATE, true);
//        }
        PreferenceUtils.setBoolean(SettingActivity.this, Config.KEY_AUTO_UPDATE, !flag);
    }

    private void clickCallSmsSafe() {
        // 如果服务开启，就关闭，否则相反
        if (ServiceStateUtils.isServiceRunning(SettingActivity.this, CallSmsSafeService.class)) {
//            就停止
            Intent intent = new Intent(SettingActivity.this, CallSmsSafeService.class);
            stopService(intent);
//            ui
            mSivAutoCallSmsSafe.setToggleState(false);
        } else {
//            就开启
            Intent intent = new Intent(SettingActivity.this, CallSmsSafeService.class);
            startService(intent);
//            ui
            mSivAutoCallSmsSafe.setToggleState(true);
        }
    }

    private void clickAutoClean() {
//       开启服务或者关闭服务
        if (ServiceStateUtils.isServiceRunning(SettingActivity.this, AutoCleanService.class)) {
//           就关闭
            Intent intent = new Intent(SettingActivity.this, AutoCleanService.class);
            stopService(intent);
//            ui
            mSivAutoClean.setToggleState(false);
        } else {
//                就开启
            Intent intent = new Intent(SettingActivity.this, AutoCleanService.class);
            startService(intent);
//            ui
            mSivAutoClean.setToggleState(true);
        }
    }

    private void clickNumberAddress() {
//        如果服务开启就关闭,否则相反
        if (ServiceStateUtils.isServiceRunning(SettingActivity.this, NumberAddressService.class)) {
//            就关闭
            Intent intent = new Intent(SettingActivity.this, NumberAddressService.class);
            stopService(intent);
//            ui
            mSivNumberAddress.setToggleState(false);
        } else {
//            开启
            Intent intent = new Intent(SettingActivity.this, NumberAddressService.class);
            startService(intent);
//            ui
            mSivNumberAddress.setToggleState(true);
        }
    }

    private void clickAddressStyle() {
//        弹出对话框
        AddressStyleDialog dialog = new AddressStyleDialog(SettingActivity.this);
        dialog.show();
    }
}
