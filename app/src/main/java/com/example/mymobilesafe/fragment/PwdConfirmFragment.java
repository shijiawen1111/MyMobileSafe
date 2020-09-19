package com.example.mymobilesafe.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.activity.AppLockActivity;
import com.example.mymobilesafe.utils.Config;
import com.example.mymobilesafe.utils.PreferenceUtils;
import com.example.mymobilesafe.view.LockPatternView;

import java.util.List;

/**
 * Created by JW.S on 2020/9/11 12:49 AM.
 */
public class PwdConfirmFragment extends Fragment implements View.OnClickListener {

    private LockPatternView mPatternView;
    private Button mBtnPre;
    private Button mBtnNext;
    private String mPassword;
    private String mConfirmPassword;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mPassword = arguments.getString("password");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pwd_confirm, container, false);
        initView(view);
        initEvent();
        return view;
    }

    private void initView(View view) {
        mPatternView = view.findViewById(R.id.fragment_patter_view);
        mBtnPre = view.findViewById(R.id.fragment_btn_pre);
        mBtnNext = view.findViewById(R.id.fragment_btn_next);
    }

    private void initEvent() {
        mBtnNext.setOnClickListener(this);
        mBtnPre.setOnClickListener(this);
        mPatternView.setOnPatternListener(new LockPatternView.OnPatternListener() {
            @Override
            public void onPatternStart() {

            }

            @Override
            public void onPatternCleared() {
                mConfirmPassword = null;
            }

            @Override
            public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {

            }

            @Override
            public void onPatternDetected(List<LockPatternView.Cell> pattern) {
                StringBuilder builder = new StringBuilder();
                // 密码获得
                // 记录密码
                for (int i = 0; i < pattern.size(); i++) {
                    LockPatternView.Cell cell = pattern.get(i);
                    int p = cell.getRow() * 3 + cell.getColumn();
                    builder.append(p);
                }
                mConfirmPassword = builder.toString();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnPre) {
            clickPre();
        } else if (v == mBtnNext) {
            lickNext();
        }
    }

    private void clickPre() {
        PwdManagerFragment fragment = (PwdManagerFragment) getParentFragment();
        fragment.popBack();
    }

    private void lickNext() {
        if (TextUtils.isEmpty(mConfirmPassword)) {
            Toast.makeText(getActivity(), "必须去设置确认密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mConfirmPassword.equals(mPassword)) {
            Toast.makeText(getActivity(), "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        PreferenceUtils.setString(getActivity(), Config.KEY_APP_LOCK_PWD, mPassword);
        Intent intent = new Intent(getActivity(), AppLockActivity.class);
        getActivity().startActivity(intent);
        getActivity().finish();
    }
}
