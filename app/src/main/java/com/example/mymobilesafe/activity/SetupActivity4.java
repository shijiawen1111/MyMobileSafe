package com.example.mymobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.mymobilesafe.R;

/**
 * Created by JW.S on 2020/5/21 11:20 AM.
 */
public class SetupActivity4 extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
    }

    public void clickPre(View view){
        Intent intent = new Intent(SetupActivity4.this, SetupActivity3.class);
        startActivity(intent);
    }

    public void clickNext(View view){
        Intent intent = new Intent(SetupActivity4.this, SetupActivity5.class);
        startActivity(intent);
    }
}
