package com.example.mymobilesafe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.business.SmsProvider;
import com.example.mymobilesafe.view.SettingItemView;

public class CommonToolActivity extends Activity implements View.OnClickListener {
    private SettingItemView mTvAddress;
    private SettingItemView mTvCommonNumber;
    private SettingItemView mTvSmsBackUp;
    private SettingItemView mTvSmsRestore;
    private SettingItemView mTvSivAppLock;
    private SettingItemView mTvSivAppLockService;
    private TextView mTvSmsProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_tool);
        initView();
        initEvent();
    }

    private void initView() {
        mTvAddress = findViewById(R.id.tv_number);
        mTvCommonNumber = findViewById(R.id.common_number);
        mTvSmsBackUp = findViewById(R.id.ac_siv_sms_backup);
        mTvSmsRestore = findViewById(R.id.ac_siv_sms_restore);
        mTvSmsProgress = findViewById(R.id.ac_siv_sms_progress);
        mTvSivAppLock = findViewById(R.id.ac_siv_app_lock);
        mTvSivAppLockService = findViewById(R.id.ac_siv_app_lock_service);
    }

    private void initEvent() {
        mTvAddress.setOnClickListener(this);
        mTvCommonNumber.setOnClickListener(this);
        mTvSmsBackUp.setOnClickListener(this);
        mTvSmsRestore.setOnClickListener(this);
        mTvSivAppLockService.setOnClickListener(this);
        mTvSivAppLock.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mTvAddress) {
            clickNumberAddress();
        } else if (v == mTvCommonNumber) {
            clickCommonNumber();
        } else if (v == mTvSmsBackUp) {
            clickSmsBackUp();
        } else if (v == mTvSmsRestore) {
            clickSmsRestore();
        } else if (v == mTvSivAppLock) {
            clickAppLock();
        } else if (v == mTvSivAppLockService) {
            clickAppLockService();
        }
    }

    private void clickCommonNumber() {
        Intent intent = new Intent(CommonToolActivity.this, CommonNumberActivity.class);
        startActivity(intent);
    }

    private void clickNumberAddress() {
        Intent intent = new Intent(CommonToolActivity.this, NumberAddressActivity.class);
        startActivity(intent);
    }

    private void clickAppLock() {
        Intent intent = new Intent(CommonToolActivity.this, AppLockSettingActivity.class);
        startActivity(intent);
    }

    private void clickAppLockService() {
        Intent intent = new Intent();
        intent.setAction("android.settings.ACCESSIBILITY_SETTINGS");
        startActivity(intent);
    }

    /**
     * 短信备份
     */
    private void clickSmsBackUp() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //所有的接口只和ui相关
        SmsProvider.smsBackUp(this, new SmsProvider.OnSmsListener() {
            @Override
            public void onPre() {
                dialog.show();
            }

            @Override
            public void onProgress(int max, int progress) {
                dialog.setMax(max);
                dialog.setProgress(progress);
            }

            @Override
            public void onFinish(boolean result) {
                dialog.dismiss();
                if (result) {
                    Toast.makeText(CommonToolActivity.this, "备份成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CommonToolActivity.this, "备份失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 短信还原
     */
    private void clickSmsRestore() {
        // 读取短信备份的文件---》对象---》插入数据 --->UI显示
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        SmsProvider.smsRetore(this, new SmsProvider.OnSmsListener() {
            @Override
            public void onPre() {
                dialog.show();
            }

            @Override
            public void onProgress(int max, int progress) {
                dialog.setMax(max);
                dialog.setProgress(progress);
            }

            @Override
            public void onFinish(boolean result) {
                dialog.dismiss();
                if (result) {
                    Toast.makeText(CommonToolActivity.this, "还原成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CommonToolActivity.this, "还原失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
