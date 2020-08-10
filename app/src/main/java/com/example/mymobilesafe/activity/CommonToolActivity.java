package com.example.mymobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.view.SettingItemView;

public class CommonToolActivity extends Activity implements View.OnClickListener {
    private SettingItemView mTvAddress;
    private SettingItemView mTvCommonNumber;


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
    }

    private void initEvent() {
        mTvAddress.setOnClickListener(this);
        mTvCommonNumber.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mTvAddress) {
            Intent intent = new Intent(CommonToolActivity.this, NumberAddressActivity.class);
            startActivity(intent);
        } else if (v == mTvCommonNumber) {
            Intent intent = new Intent(CommonToolActivity.this, CommonNumberActivity.class);
            startActivity(intent);
        }
    }
}
