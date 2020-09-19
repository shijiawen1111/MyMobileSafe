package com.example.mymobilesafe.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.view.LockPatternView;

import java.util.List;

/**
 * Created by JW.S on 2020/9/11 12:43 AM.
 */
public class PwdSettingFragment extends Fragment implements View.OnClickListener {

    private LockPatternView mPatterView;
    private Button mBtnPre;
    private Button mBtnNext;
    private String mPassWord;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pwd_setting, container, false);
        initView(view);
        initEvent();
        return view;
    }

    private void initView(View view) {
        mPatterView = view.findViewById(R.id.fragment_patter_view);
        mBtnPre = view.findViewById(R.id.fragment_btn_pre);
        mBtnNext = view.findViewById(R.id.fragment_btn_next);
    }

    private void initEvent() {
        mBtnPre.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        mPatterView.setOnPatternListener(new LockPatternView.OnPatternListener() {
            @Override
            public void onPatternStart() {

            }

            @Override
            public void onPatternCleared() {
                mPassWord = null;
            }

            @Override
            public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {

            }

            @Override
            public void onPatternDetected(List<LockPatternView.Cell> pattern) {
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < pattern.size(); i++) {
                    LockPatternView.Cell cell = pattern.get(i);
                    int p = cell.getRow() * 3 + cell.getColumn();
                    sb.append(p);
                }
                mPassWord = sb.toString();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnPre) {
            clickPre();
        } else if (v == mBtnNext) {
            clickNext();
        }
    }

    /**
     * 回退的操作
     */
    private void clickPre() {
        PwdManagerFragment fragment = (PwdManagerFragment) getParentFragment();
        fragment.popBack();
    }

    /**
     * 去设置密码的操作
     */
    private void clickNext() {
        if (TextUtils.isEmpty(mPassWord)){
            Toast.makeText(getActivity(), "必须去设置密码", Toast.LENGTH_SHORT).show();
            return;
        }
        //切换到设置密码的fragment中
        PwdManagerFragment  fragment = (PwdManagerFragment) getParentFragment();
        fragment.loadPwdConfirmFragment(mPassWord);
    }
}
