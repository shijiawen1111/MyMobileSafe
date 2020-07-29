package com.example.mymobilesafe.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.example.mymobilesafe.R;
import com.example.mymobilesafe.receiver.SjfdAdminReceiver;

/**
 * Created by JW.S on 2020/5/21 11:20 AM.
 */
public class SetupActivity4 extends BaseSetUpActivity implements View.OnClickListener {
    private static final String TAG = "SetupActivity4";
    private static final int REQUEST_CODE_ENABLE_ADMIN = 0;
    private DevicePolicyManager mDpm;
    private RelativeLayout mRlAdmin;
    private ImageView mIvAdmin;

    @SuppressLint("ServiceCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        mDpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        initView();
        initEvent();
    }

    private void initView() {
        mRlAdmin = findViewById(R.id.setup4_rl_admin);
        mIvAdmin = findViewById(R.id.setup4_iv_admin);
        ComponentName mCn = new ComponentName(this, SjfdAdminReceiver.class);
        //初始化时,显示正确的图片状态
        mIvAdmin.setImageResource(mDpm.isAdminActive(mCn) ? R.drawable.admin_activated : R.drawable.admin_inactivated);
    }

    private void initEvent() {
        mRlAdmin.setOnClickListener(this);
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
        //判断是否激活的设备管理员
        ComponentName mCn = new ComponentName(this, SjfdAdminReceiver.class);
        if (!mDpm.isAdminActive(mCn)){
            //需要中断
            Toast.makeText(this, "如果需要开启手机防盗,务必激活设备管理员", Toast.LENGTH_SHORT).show();
            return true;
        }
        Intent intent = new Intent(SetupActivity4.this, SetupActivity5.class);
        startActivity(intent);
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v == mRlAdmin) {
            clickAdmin();
        }
    }

    private void clickAdmin() {
        ComponentName mCn = new ComponentName(this, SjfdAdminReceiver.class);
        if (!mDpm.isAdminActive(mCn)) {
            //没有激活,则去打开激活界面
            Log.d(TAG, "没有激活");
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mCn);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.add_admin_extra_app_text));
            startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
        } else {
            //已经激活
            mDpm.removeActiveAdmin(mCn);
            Log.d(TAG, "已经激活：：");
            //UI显示
            mIvAdmin.setImageResource(R.drawable.admin_inactivated);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
            if (resultCode == Activity.RESULT_OK) {
                //激活操作
                //UI显示
                mIvAdmin.setImageResource(R.drawable.admin_activated);
            }
        }
    }
}
