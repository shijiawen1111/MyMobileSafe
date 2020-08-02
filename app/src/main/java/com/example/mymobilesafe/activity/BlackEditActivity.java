package com.example.mymobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.bean.BlackBean;
import com.example.mymobilesafe.db.BlackDao;

public class BlackEditActivity extends Activity implements View.OnClickListener {

    public static final String ACTION_ADD = "add";
    public static final String KEY_NUMBER = "number";
    public static final String KEY_TYPE = "type";
    public static final String ACTION_UPDATE = "update";
    public static final String KEY_POSITION = "position";
    private TextView mTvTitle;
    private EditText mEtNumber;
    private RadioGroup mRgType;
    private Button mBtnOk;
    private Button mBtnCancer;
    private BlackDao mDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_edit);
        mDao = new BlackDao(this);
        initView();
        initEvent();
    }

    private void initView() {
        mTvTitle = findViewById(R.id.be_tv_title);
        mEtNumber = findViewById(R.id.be_et_number);
        mRgType = findViewById(R.id.be_rg_type);
        mBtnOk = findViewById(R.id.be_btn_ok);
        mBtnCancer = findViewById(R.id.be_btn_cancer);
        // 根据进入的行为来判断UI显示
        Intent intent = getIntent();
        String action = intent.getAction();
        if (ACTION_ADD.equals(action)){
            // 添加的行为进入
            mTvTitle.setText("添加黑名单");
            mEtNumber.setEnabled(true);
            mBtnOk.setTag("保存");
        }else {
            // 以update行为进入
            mTvTitle.setText("更新黑名单");
            mEtNumber.setEnabled(false);
            mEtNumber.setText(intent.getStringExtra(KEY_NUMBER));
            // 选中已经选中的radiobutton
            int type = intent.getIntExtra(KEY_TYPE, -1);//call-0
            int id = -1;
            switch (type){
                case BlackBean.TYPE_CALL:           //电话
                id = R.id.be_rb_call;
                break;
                case BlackBean.TYPE_SMS:            //短信
                    id = R.id.be_rb_sms;
                    break;
                case BlackBean.TYPE_ALL:            //全部
                    id = R.id.be_rb_all;
                    break;
                default:
                    break;
            }
            mRgType.check(id);
            mBtnOk.setTag("更新");
        }
    }
    private void initEvent() {
        mBtnOk.setOnClickListener(this);
        mBtnCancer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnOk){
            clickOk();
        }else if (v == mBtnCancer){
            clickCancer();
        }
    }

    private void clickOk() {
        // 1.校验输入框
        String number = mEtNumber.getText().toString().trim();
        if (TextUtils.isEmpty(number)){
            Toast.makeText(this, "请输入号码", Toast.LENGTH_SHORT).show();
            return;
        }
        // 2.校验radiogroup
        int radioButtonId = mRgType.getCheckedRadioButtonId();
        // 一个都没有选中
        if (radioButtonId == -1){
            Toast.makeText(this, "请选择拦截类型", Toast.LENGTH_SHORT).show();
            return;
        }
        int type = -1;//电话-0，短信-1,全部-2
        switch (radioButtonId){
            case R.id.be_rb_call:
                //电话
                type = BlackBean.TYPE_CALL;
                break;
            case R.id.be_rb_sms:
                //短信
                type = BlackBean.TYPE_SMS;
                break;
            case R.id.be_rb_all:
                type = BlackBean.TYPE_ALL;
                break;
            default:
                break;
        }
        Intent intent = getIntent();
        String action = intent.getAction();
        int position = intent.getIntExtra(KEY_POSITION, -1);
        if (ACTION_ADD.equals(action)){
            // 添加的逻辑
            // 添加黑名单
            boolean success = mDao.add(number, type);
            if (success){
                Toast.makeText(this, "添加成功!", Toast.LENGTH_SHORT).show();
                Intent data = new Intent();
                data.putExtra(KEY_NUMBER, number);
                data.putExtra(KEY_TYPE, type);
                setResult(Activity.RESULT_OK, data);
            }else {
                Toast.makeText(this, "添加失败!", Toast.LENGTH_SHORT).show();
            }
            finish();
        }else {
            // 更新的逻辑
            boolean update = mDao.update(number, type);
            if (update){
                Toast.makeText(this, "更新成功!", Toast.LENGTH_SHORT).show();
                Intent data = new Intent();
                data.putExtra(KEY_POSITION, position);
                data.putExtra(KEY_TYPE, type);
                setResult(Activity.RESULT_OK, data);
            }else {
                Toast.makeText(this, "更新失败!", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    private void clickCancer() {
        finish();
    }
}
