package com.example.mymobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import com.example.mymobilesafe.R;

public class SetupActivity5 extends BaseSetUpActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup5);
    }

    /**
     * 跳转到到设置向导4界面
     * @param view
     */
//    public void clickPre(View view) {
//        Intent intent = new Intent(SetupActivity5.this, SetupActivity4.class);
//        startActivity(intent);
//        //        设置过度动画
//        int enterAnim = R.anim.pre_enter;   //后退的activity对应的动画
//        int exitAnim = R.anim.pre_exit;     //结束的activity对应的动画
//        overridePendingTransition(enterAnim, exitAnim);
//        finish();
//    }

    /**
     * 跳转到手机防盗界面
     * @param view
     */
//    public void clickNext(View view) {
//        Intent intent = new Intent(SetupActivity5.this, SjfdActivity.class);
//        startActivity(intent);
//        //        设置过度动画
//        int enterAnim = R.anim.next_enter;//进入的activity对应的动画
//        int exitAnim = R.anim.next_exit;  //结束的activity对应的动画
//        overridePendingTransition(enterAnim, exitAnim);
//        finish();
//    }

    @Override
    protected boolean performPre() {
        Intent intent = new Intent(SetupActivity5.this, SetupActivity4.class);
        startActivity(intent);
        return false;
    }

    @Override
    protected boolean performNext() {
        Intent intent = new Intent(SetupActivity5.this, SjfdActivity.class);
        startActivity(intent);
        return false;
    }
}
