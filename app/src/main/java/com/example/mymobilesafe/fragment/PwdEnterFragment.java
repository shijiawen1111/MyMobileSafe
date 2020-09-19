package com.example.mymobilesafe.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.activity.AppLockActivity;
import com.example.mymobilesafe.activity.AppLockSettingActivity;
import com.example.mymobilesafe.utils.Config;
import com.example.mymobilesafe.utils.PreferenceUtils;
import com.example.mymobilesafe.view.LockPatternView;

import java.util.List;

/**
 * Created by JW.S on 2020/9/10 10:55 PM.
 */
public class PwdEnterFragment extends Fragment {

    private LockPatternView mPatternView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pwd_enter, container, false);
        initView(view);
        initEvent();
        return view;
    }

    private void initView(View view) {
        mPatternView = view.findViewById(R.id.fragment_patter_view);
        //设置显示title
        AppLockSettingActivity activity = (AppLockSettingActivity) getActivity();
        activity.setAppLockTitle("输入密码");
    }

    private void initEvent() {
        mPatternView.setOnPatternListener(new LockPatternView.OnPatternListener() {
            @Override
            public void onPatternStart() {

            }

            @Override
            public void onPatternCleared() {

            }

            @Override
            public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {

            }

            @Override
            public void onPatternDetected(List<LockPatternView.Cell> pattern) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < pattern.size(); i++) {
                    LockPatternView.Cell cell = pattern.get(i);
                    int p = cell.getRow() * 3 + cell.getColumn();
                    builder.append(p);
                }
                String mPassword = builder.toString();
                //密码对比
                String pwd = PreferenceUtils.getString(getActivity(), Config.KEY_APP_LOCK_PWD);
                if (!pwd.equals(mPassword)) {
                    //不同
                    mPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                    return;
                }
                //正确,则页面跳转
                Intent intent = new Intent(getActivity(), AppLockActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });
    }
}
