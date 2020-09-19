package com.example.mymobilesafe.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.view.LockPatternView;

/**
 * Created by JW.S on 2020/9/11 12:30 AM.
 */
public class PwdSetupFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "PwdSetupFragment";
    private LockPatternView mPatternView;
    private Button mBtnNext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pwd_setup, container, false);
        initView(view);
        //点击事件
        initEvent();
        return view;
    }

    private void initView(View view) {
        mPatternView = view.findViewById(R.id.fragment_patter_view);
        mBtnNext = view.findViewById(R.id.fragment_btn_next);
        //设置图案View不可使用,要使用动画
        mPatternView.disableInput();
        mPatternView.setPattern(LockPatternView.DisplayMode.Animate, "0368");
    }

    private void initEvent() {
        mBtnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnNext) {
            clickNext();
        }
    }

    private void clickNext() {
        // 切换到密码设置的fragment中
        PwdManagerFragment fragment = (PwdManagerFragment) getParentFragment();
        fragment.loadPwdSettingFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "manager fragment destroy");
    }
}
