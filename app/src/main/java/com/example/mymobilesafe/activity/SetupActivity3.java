package com.example.mymobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mymobilesafe.R;

public class SetupActivity3 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
    }

    /**
     * 返回设置向导界面2
     * @param view
     */
    public void clickPre(View view) {
        Intent intent = new Intent(SetupActivity3.this, SetupActivity2.class);
        startActivity(intent);
    }

    /**
     * 跳转到设置向导界面4
     * @param view
     */
    public void clickNext(View view) {
        Intent intent = new Intent(SetupActivity3.this, SetupActivity4.class);
        startActivity(intent);
    }
}
