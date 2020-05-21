package com.example.mymobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mymobilesafe.R;

public class SetupActivity5 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup5);
    }

    /**
     * 跳转到到设置向导4界面
     * @param view
     */
    public void clickPre(View view) {
        Intent intent = new Intent(SetupActivity5.this, SetupActivity4.class);
        startActivity(intent);
    }

    /**
     * 跳转到手机防盗界面
     * @param view
     */
    public void clickNext(View view) {
        Intent intent = new Intent(SetupActivity5.this, SjfdActivity.class);
        startActivity(intent);
    }
}
