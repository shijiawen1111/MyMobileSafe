package com.example.mymobilesafe.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mymobilesafe.R;

public class SetupActivity1 extends BaseSetUpActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }

    /**
     * 跳转到设置向导2界面
     * @param view
     */
//    public void clickNext(View view) {
//        Intent intent = new Intent(SetupActivity1.this, SetupActivity2.class);
//        startActivity(intent);
//        // 设置过渡动画
//        int enterAnim = R.anim.next_enter;// 进入的activity对应的动画资源
//        int exitAnim = R.anim.next_exit;  //结束的activity对应的动画资源
//        overridePendingTransition(enterAnim, exitAnim);
//        finish();
//    }

    @Override
    protected boolean performPre() {
        return true;
    }

    @Override
    protected boolean performNext() {
        Intent intent = new Intent(SetupActivity1.this, SetupActivity2.class);
        startActivity(intent);
        return false;
    }
}
