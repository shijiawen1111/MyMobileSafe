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
public class SetupActivity4 extends BaseSetUpActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
    }

//    public void clickPre(View view){
//        Intent intent = new Intent(SetupActivity4.this, SetupActivity3.class);
//        startActivity(intent);
//        //        设置过度动画
//        int enterAnim = R.anim.pre_enter;   //后退的activity对应的动画
//        int exitAnim = R.anim.pre_exit;     //结束的activity对应的动画
//        overridePendingTransition(enterAnim, exitAnim);
//        finish();
//    }

//    public void clickNext(View view){
//        Intent intent = new Intent(SetupActivity4.this, SetupActivity5.class);
//        startActivity(intent);
//        //        设置过度动画
//        int enterAnim = R.anim.next_enter;//进入的activity对应的动画
//        int exitAnim = R.anim.next_exit;  //结束的activity对应的动画
//        overridePendingTransition(enterAnim, exitAnim);
//        finish();
//    }

    @Override
    protected boolean performPre() {
        Intent intent = new Intent(SetupActivity4.this, SetupActivity3.class);
        startActivity(intent);
        return false;
    }

    @Override
    protected boolean performNext() {
        Intent intent = new Intent(SetupActivity4.this, SetupActivity5.class);
        startActivity(intent);
        return false;
    }
}
