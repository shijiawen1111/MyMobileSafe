package com.example.mymobilesafe.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mymobilesafe.R;

public class SetupActivity1 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }

    /**
     * 跳转到设置向导2界面
     * @param view
     */
    public void clickNext(View view){
        Intent intent = new Intent(SetupActivity1.this, SetupActivity2.class);
        startActivity(intent);
    }
}
