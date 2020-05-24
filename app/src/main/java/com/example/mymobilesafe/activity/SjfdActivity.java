package com.example.mymobilesafe.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.utils.Config;
import com.example.mymobilesafe.utils.PreferenceUtils;

public class SjfdActivity extends Activity implements View.OnClickListener {

    private TextView mTvsjfdNumber;
    private RelativeLayout mRlSjfdProtected;
    private ImageView mIvSjfdProtected;
    private RelativeLayout mRlSjfdSetup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sjfd);
        initView();
        initEvent();
    }

    private void initView() {
        mTvsjfdNumber = findViewById(R.id.sjfd_tv_number);
        mRlSjfdProtected = findViewById(R.id.sjfd_rl_protecting);
        mIvSjfdProtected = findViewById(R.id.sjfd_iv_protecting);
        mRlSjfdSetup = findViewById(R.id.sjfd_rl_setup);
        String number = PreferenceUtils.getString(SjfdActivity.this, Config.KEY_SJFD_NUMBER);
        mTvsjfdNumber.setText(number);
        // 设置是否开启防盗保护
        boolean protecting = PreferenceUtils.getBoolean(SjfdActivity.this, Config.KEY_SJFD_SETUP);
        mIvSjfdProtected.setImageResource(protecting ? R.drawable.lock : R.drawable.unlock);
    }

    private void initEvent() {
        mRlSjfdProtected.setOnClickListener(this);
        mRlSjfdSetup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sjfd_rl_protecting:
                clickProtecting();
                break;
            case R.id.sjfd_rl_setup:
                clickSetup();
                break;
            default:
                break;
        }
    }

    private void clickProtecting() {
        // 如果当前是已经开启防盗保护，点击时就取消保护，否则相反
        boolean protecting = PreferenceUtils.getBoolean(SjfdActivity.this, Config.KEY_SJFD_PROTECTING);
        if (protecting) {
            // 取消防盗保护
            // UI显示
            mIvSjfdProtected.setImageResource(R.drawable.unlock);
            // 数据存储
            PreferenceUtils.setBoolean(SjfdActivity.this, Config.KEY_SJFD_PROTECTING, false);
        } else {
            // 开启保护
            // UI显示
            mIvSjfdProtected.setImageResource(R.drawable.lock);
            // 数据存储
            PreferenceUtils.setBoolean(SjfdActivity.this, Config.KEY_SJFD_PROTECTING, true);
        }
    }

    private void clickSetup() {
        Intent intent = new Intent(SjfdActivity.this, SetupActivity1.class);
        startActivity(intent);
        finish();
    }
}
