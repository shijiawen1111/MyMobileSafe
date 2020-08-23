package com.example.mymobilesafe.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mymobilesafe.R;

/**
 * Created by JW.S on 2020/8/16 8:19 PM.
 * 自定义一个具有水平进度条和三个TextView 的组合控件ProgressStateView
 */
public class ProgressStateView extends RelativeLayout {
    private TextView mTvText;
    private TextView mTvLeft;
    private TextView mTvRight;
    private ProgressBar mPbProgress;

    public ProgressStateView(Context context) {
        this(context, null);
    }

    public ProgressStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 加载布局
        View.inflate(context, R.layout.view_progress_state, this);
        // 初始化view
        initView();
        // 设置属性值
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ProgressStateView);
        String text = ta.getString(R.styleable.ProgressStateView_psvText);
        ta.recycle();
        mTvText.setText(text);
    }

    private void initView() {
        mTvText = findViewById(R.id.tv_title);
        mTvLeft = findViewById(R.id.tv_left);
        mTvRight = findViewById(R.id.tv_right);
        mPbProgress = findViewById(R.id.pb_progress);
    }

    /**
     * 设置左边的文本
     * @param text
     */
    public void setLeftText(String text){
        mTvLeft.setText(text);
    }

    /**
     * 设置右边的文本
     * @param text
     */
    public void setRightText(String text){
        mTvRight.setText(text);
    }

    /**
     * 设置当前的进度
     * @param progress
     */
    public void setCurrentProgress(int progress){
        mPbProgress.setProgress(progress);
    }

    /**
     * 设置最大进度
     * @param maxProgress
     */
    public void setMaxProgress(int maxProgress){
        mPbProgress.setProgress(maxProgress);
    }


}
