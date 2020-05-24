package com.example.mymobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.utils.Config;
import com.example.mymobilesafe.utils.PreferenceUtils;

public class SetupActivity5 extends BaseSetUpActivity {

    private CheckBox mCbProtecting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup5);
        initView();
    }

    private void initView() {
        mCbProtecting = findViewById(R.id.setup5_cb_protecting);
        mCbProtecting.setChecked(PreferenceUtils.getBoolean(SetupActivity5.this, Config.KEY_SJFD_PROTECTING));
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
     *
     * @param
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
        // 如果checkbox没有选中，提示要选中
        if (!mCbProtecting.isChecked()) {
            Toast.makeText(this, "勾选后才能开启防盗保护", Toast.LENGTH_SHORT).show();
            return true;//终止
        }
        // 数据的操作，存储是否开启了防盗保护
        PreferenceUtils.setBoolean(SetupActivity5.this, Config.KEY_SJFD_PROTECTING, true);
        // 存储已经设置过了设置向导
        PreferenceUtils.setBoolean(SetupActivity5.this, Config.KEY_SJFD_SETUP, true);
        Intent intent = new Intent(SetupActivity5.this, SjfdActivity.class);
        startActivity(intent);
        return false;
    }
}
