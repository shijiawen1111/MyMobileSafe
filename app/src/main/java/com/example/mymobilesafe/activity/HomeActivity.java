package com.example.mymobilesafe.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.mymobilesafe.R;

public class HomeActivity extends Activity {

    private ImageView mIvLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        初始化控件
        initView();
//        让logo进行动画
        doLogoAnimation();
    }

    private void initView() {
        mIvLogo = findViewById(R.id.home_iv_logo);
    }

    private void doLogoAnimation() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mIvLogo, "rotationY", 0, 90, 180, 270, 360, 180, 270);
        animator.setDuration(3000);//设置动画时长
        animator.setRepeatCount(ObjectAnimator.INFINITE);//设置可重复
        animator.setRepeatMode(ObjectAnimator.REVERSE);//设置重复模式
        animator.start();
    }

    public void onClickSetting(View view) {

    }
}
