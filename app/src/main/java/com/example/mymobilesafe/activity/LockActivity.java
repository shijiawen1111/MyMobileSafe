package com.example.mymobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.utils.Config;
import com.example.mymobilesafe.utils.PackageUtils;
import com.example.mymobilesafe.utils.PreferenceUtils;
import com.example.mymobilesafe.view.LockPatternView;

import java.util.List;

/**
 * Created by JW.S on 2020/9/17 11:17 AM.
 */
public class LockActivity extends Activity {

    private ImageView mIvIcon;
    private TextView mTvName;
    private LockPatternView mPatterView;
    private String mPackageName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        initView();
        initEvent();
    }

    private void initView() {
        mIvIcon = findViewById(R.id.lock_iv_icon);
        mTvName = findViewById(R.id.lock_tv_name);
        mPatterView = findViewById(R.id.lock_patter_view);
        mPackageName = getIntent().getStringExtra("pkg");
        PackageManager pm = getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(mPackageName, 0);
            //设置图标,应用名称
            Drawable icon = PackageUtils.getAppIcon(this, info);
            String name = PackageUtils.getAppName(this, info);
            mIvIcon.setImageDrawable(icon);
            mTvName.setText(name);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initEvent() {
        mPatterView.setOnPatternListener(new LockPatternView.OnPatternListener() {
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
                //设置密码校验
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < pattern.size(); i++) {
                    LockPatternView.Cell cell = pattern.get(i);
                    int p = cell.getRow() * 3 + cell.getColumn();
                    builder.append(p);
                }
                String password = builder.toString();
                //对比密码
                String pwd = PreferenceUtils.getString(LockActivity.this, Config.KEY_APP_LOCK_PWD);
                if (!pwd.equals(password)) {
                    //不同
                    mPatterView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                    return;
                }
                //通知程序锁的服务去停止当前对应用的拦截,发送广播
                Intent intent = new Intent();
                intent.setAction("com.example.mymobilesafe");
                intent.putExtra("pkg", mPackageName);
                sendBroadcast(intent);
                //让当前的activity销毁,显示上锁的程序
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //让当前应用进入桌面
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
    }
}
