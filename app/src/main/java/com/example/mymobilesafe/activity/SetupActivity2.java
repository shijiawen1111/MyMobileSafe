package com.example.mymobilesafe.activity;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.utils.Config;
import com.example.mymobilesafe.utils.PreferenceUtils;

public class SetupActivity2 extends BaseSetUpActivity implements View.OnClickListener {
    private static final String TAG = "SetupActivity2";
    private RelativeLayout mRlBind;
    private ImageView mIVBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        initView();
        initEvent();
    }

    private void initEvent() {
        mRlBind.setOnClickListener(this);
    }

    private void initView() {
        mRlBind = findViewById(R.id.setup2_rl_bind);
        mIVBind = findViewById(R.id.setup2_iv_bind);
        // 设置绑定状态
        String sim = PreferenceUtils.getString(SetupActivity2.this, Config.KEY_SJFD_SIM);
        mIVBind.setImageResource(TextUtils.isEmpty(sim) ? R.drawable.unlock : R.drawable.lock);
    }

//    public void clickPre(View view) {
//        Intent intent = new Intent(SetupActivity2.this, SetupActivity1.class);
//        startActivity(intent);
//
//        //        设置过度动画
//        int enterAnim = R.anim.pre_enter;//后退的activity对应的动画资源
//        int exitAnim = R.anim.pre_exit;  //结束的activity对应的动画资源
//        overridePendingTransition(enterAnim, exitAnim);
//        finish();
//    }

//    public void clickNext(View view) {
//        Intent intent = new Intent(SetupActivity2.this, SetupActivity3.class);
//        startActivity(intent);
//        // 设置过渡动画
//        int enterAnim = R.anim.next_enter;// 进入的activity对应的动画资源
//        int exitAnim = R.anim.next_exit;  // 结束的activity对应的动画资源
//        overridePendingTransition(enterAnim, exitAnim);
//        finish();
//    }

    @Override
    protected boolean performPre() {
        Intent intent = new Intent(SetupActivity2.this, SetupActivity1.class);
        startActivity(intent);
        return false;
    }

    @Override
    protected boolean performNext() {
        // 获得存储的sim卡数据
        String simInfo = PreferenceUtils.getString(SetupActivity2.this, Config.KEY_SJFD_SIM);
        // 校验sim卡是否绑定
        if (TextUtils.isEmpty(simInfo)) {
            Toast.makeText(this, "如果要开启防盗保护,必须绑定SIM卡", Toast.LENGTH_SHORT).show();
//            中断进入到下一个界面
            return true;
        }
        Intent intent = new Intent(SetupActivity2.this, SetupActivity3.class);
        startActivity(intent);
        return false;
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "v: " + v);
        Log.d(TAG, "mIVBind: " + mIVBind);
        if (v == mRlBind) {
            clickBind();
        }
    }

    /**
     * 1.如果绑定就解绑,如果没有就绑定
     */
    private void clickBind() {
        String sim = PreferenceUtils.getString(SetupActivity2.this, Config.KEY_SJFD_SIM);
        if (!TextUtils.isEmpty(sim)) {
            // UI--》用户看的
            mIVBind.setImageResource(R.drawable.unlock);
            // 数据-->开发记录标记
            // 清理存储的数据
            PreferenceUtils.setString(SetupActivity2.this, Config.KEY_SJFD_SIM, null);
        } else {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                ActivityCompat.requestPermissions(SetupActivity2.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            sim = tm.getSimSerialNumber();
            mIVBind.setImageResource(R.drawable.lock);
            // 记录sim卡信息
            PreferenceUtils.setString(SetupActivity2.this, Config.KEY_SJFD_SIM, sim);
        }
    }
}
