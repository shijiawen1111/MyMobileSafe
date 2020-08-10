package com.example.mymobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.db.NumberAddressDao;

public class NumberAddressActivity extends Activity implements View.OnClickListener {
    private EditText mEtNumber;
    private Button mBtnQuery;
    private TextView mTvAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_address);
        initView();
        initEvent();
    }

    private void initView() {
        mEtNumber = findViewById(R.id.et_number);
        mBtnQuery = findViewById(R.id.number_btn_query);
        mTvAddress = findViewById(R.id.number_tv_address);
    }

    private void initEvent() {
        mBtnQuery.setOnClickListener(this);
        //给输入框注册一个文本变化的监听器
        mEtNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String number = mEtNumber.getText().toString().trim();
                if (TextUtils.isEmpty(number)) {
                    return;
                }
                String address = NumberAddressDao.findAddress(NumberAddressActivity.this, number);
                mTvAddress.setText("归属地:" + address);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnQuery) {
            clickQuery();
        }
    }

    private void clickQuery() {
        // 如果输入框没有内容，就抖动
        String number = mEtNumber.getText().toString().trim();
        if (TextUtils.isEmpty(number)) {

            // 抖动
            // Animation shake = AnimationUtils.loadAnimation(this,
            // R.anim.shake);
            // mEtNumber.startAnimation(shake);
            // android 所有的xml类型的文件其实会映射成java代码
            TranslateAnimation animation = new TranslateAnimation(0, 10, 0, 0);
            animation.setDuration(1000);
            animation.setInterpolator(new CycleInterpolator(10));
            mEtNumber.startAnimation(animation);
            // 不能为空的提示
            Toast.makeText(this, "号码不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }

        String address = NumberAddressDao.findAddress(this, number);
        mTvAddress.setText("归属地:" + address);
    }
}
