package com.example.mymobilesafe.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.mymobilesafe.R;

public class SrljActivity extends Activity implements View.OnClickListener {
    private ImageView mCssIvAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_srlj);
        initView();
        initEvent();
    }

    private void initView() {
        mCssIvAdd = findViewById(R.id.css_iv_add);
    }

    private void initEvent() {
        mCssIvAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(SrljActivity.this, BlackEditActivity.class);
        startActivity(intent);
    }
}
