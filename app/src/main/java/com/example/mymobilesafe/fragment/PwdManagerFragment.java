package com.example.mymobilesafe.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.activity.AppLockSettingActivity;

/**
 * Created by JW.S on 2020/9/10 10:52 PM.
 * 密码设置的管理界面
 */
public class PwdManagerFragment extends Fragment {
    public static final String TAG = "PwdManagerFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pwd_maneger, container, false);
        // 管理显示Fragment
        // 默认显示PwdSetupFragment
        loadPwdSetupFragment();
        return view;
    }

    //当activity创建的时候回调
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 设置回退栈的监听
        doBackStackListener();
        // 管理显示Fragment
        // 默认显示PwdSetupFragment设置向导界面
        loadPwdSetupFragment();
    }

    @SuppressLint("ResourceType")
    private void loadPwdSetupFragment() {
        FragmentManager fm = getChildFragmentManager();
        // 开启事务
        FragmentTransaction transaction = fm.beginTransaction();

        // 设置动画
        transaction.setCustomAnimations(0, R.anim.f_exit, R.anim.f_pop_enter,
                R.anim.f_pop_exit);

        // 设置fragment
        transaction.replace(R.id.pwd_manager_container, new PwdSetupFragment());

        // 添加到回退栈
        transaction.addToBackStack("setup");

        // 提交事务
        transaction.commit();
    }

    private void doBackStackListener() {
        final FragmentManager fm = getChildFragmentManager();
        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                //当回退栈发生改变时,获得顶部的fragment
                int count = fm.getBackStackEntryCount();
                Log.d(TAG, "回退栈发生改变 count:" + count);
                if (count > 0) {
                    FragmentManager.BackStackEntry entry = fm.getBackStackEntryAt(count - 1);
                    String name = entry.getName();
                    if ("setup".equals(name)) {
                        setAppLockTitle("密码设置向导");
                    } else if ("setting".equals(name)) {
                        setAppLockTitle("密码设置");
                    } else if ("confirm".equals(name)) {
                        setAppLockTitle("密码确认");
                    }
                }
            }
        });
    }

    //设置activity的title
    private void setAppLockTitle(String title) {
        AppLockSettingActivity activity = (AppLockSettingActivity) getActivity();
        activity.setAppLockTitle(title);
    }

    //显示设置密码界面
    @SuppressLint("ResourceType")
    public void loadPwdSettingFragment() {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        // 设置动画
        transaction.setCustomAnimations(R.anim.f_enter, R.anim.f_exit,
                R.anim.f_pop_enter, R.anim.f_pop_exit);
        transaction.replace(R.id.pwd_manager_container,
                new PwdSettingFragment());
        // 添加到回退栈
        transaction.addToBackStack("setting");
        transaction.commit();
    }

    //显示确认密码界面
    @SuppressLint("ResourceType")
    public void loadPwdConfirmFragment(String password) {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        // 设置动画
        transaction.setCustomAnimations(R.anim.f_enter, R.anim.f_exit,
                R.anim.f_pop_enter, R.anim.f_pop_exit);

        PwdConfirmFragment fragment = new PwdConfirmFragment();
        Bundle args = new Bundle();
        args.putString("password", password);
        fragment.setArguments(args);

        transaction.replace(R.id.pwd_manager_container, fragment);
        // 添加到回退栈
        transaction.addToBackStack("confirm");
        transaction.commit();
    }

    public void popBack() {
        FragmentManager fm = getChildFragmentManager();
        fm.popBackStack();
    }
}
