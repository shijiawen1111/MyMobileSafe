package com.example.mymobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.utils.Config;
import com.example.mymobilesafe.utils.PreferenceUtils;

public class SetupActivity3 extends BaseSetUpActivity {
    private static final String TAG = "SetupActivity3";
    private static final int REQUEST_NUMBER = 100;
    private EditText mEtNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        initView();
    }

    private void initView() {
        mEtNumber = findViewById(R.id.setup3_et_number);
        // 回显安全号码
        String number = PreferenceUtils.getString(SetupActivity3.this, Config.KEY_SJFD_NUMBER);
        mEtNumber.setText(number);
        if (!TextUtils.isEmpty(number)) {
//            移动光标
            mEtNumber.setSelection(number.length());
        }
    }


    /**
     * 返回设置向导界面2
     * @param view
     */
//    public void clickPre(View view) {
//        Intent intent = new Intent(SetupActivity3.this, SetupActivity2.class);
//        startActivity(intent);
////        设置过度动画
//        int enterAnim = R.anim.pre_enter;   //后退的activity对应的动画
//        int exitAnim = R.anim.pre_exit;     //结束的activity对应的动画
//        overridePendingTransition(enterAnim, exitAnim);
//        finish();
//    }

    /**
     * 跳转到设置向导界面4
     *
     * @param view
     */
//    public void clickNext(View view) {
//        Intent intent = new Intent(SetupActivity3.this, SetupActivity4.class);
//        startActivity(intent);
////        设置过度动画
//        int enterAnim = R.anim.next_enter;//进入的activity对应的动画
//        int exitAnim = R.anim.next_exit;  //结束的activity对应的动画
//        overridePendingTransition(enterAnim, exitAnim);
//        finish();
//    }
    public void clickContact(View view) {
        Intent intent = new Intent(SetupActivity3.this, ContactSelectActivity.class);
//        要数据回来
        startActivityForResult(intent, REQUEST_NUMBER);
    }

    @Override
    protected boolean performPre() {
        Intent intent = new Intent(SetupActivity3.this, SetupActivity2.class);
        startActivity(intent);
        return false;
    }

    @Override
    protected boolean performNext() {
        // 校验输入框
        String mEtNumber = this.mEtNumber.getText().toString().trim();
        if (TextUtils.isEmpty(mEtNumber)){
            Toast.makeText(this, "若要开启手机防盗,必须设置安全号码!", Toast.LENGTH_SHORT).show();
            return true;//终端操作
        }
        // 存储安全号码
        PreferenceUtils.setString(SetupActivity3.this, Config.KEY_SJFD_NUMBER, mEtNumber);

        // 页面跳转
        Intent intent = new Intent(SetupActivity3.this, SetupActivity4.class);
        startActivity(intent);
        // 不需要中断
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拿到数据返回的地方
        if (requestCode == REQUEST_NUMBER && resultCode == Activity.RESULT_OK) {
            String number = data.getStringExtra(ContactSelectActivity.KEY_NUMBER);
            Log.d(TAG, "number: " + number);
            mEtNumber.setText(number);
//            设置移动光标
            if (!TextUtils.isEmpty(number)) {
                mEtNumber.setSelection(number.length());
            }
        }
    }
}
