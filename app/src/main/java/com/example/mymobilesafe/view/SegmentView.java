package com.example.mymobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.mymobilesafe.R;

/**
 * Created by JW.S on 2020/9/13 7:50 PM.
 */
public class SegmentView extends LinearLayout implements View.OnClickListener {

    private TextView mTvLeft;
    private TextView mTvRight;
    private boolean mLeftSelected;
    private OnCheckListener mListener;

    public SegmentView(Context context) {
        this(context, null);
    }

    public SegmentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //挂载xml
        View.inflate(context, R.layout.view_segment, this);
        //初始化控件
        mTvLeft = findViewById(R.id.segment_tv_left);
        mTvRight = findViewById(R.id.segment_tv_right);
        //设置默认选中
        mTvLeft.setSelected(true);
        mTvRight.setSelected(false);
        mLeftSelected = true;
        //初始化控件
        initEvent();
    }

    private void initEvent() {
        mTvLeft.setOnClickListener(this);
        mTvRight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mTvLeft) {
            //如果左侧没选中,才能被选中
            if (mLeftSelected) {
                return;
            }
            //点击左侧,让左侧选中
            mTvLeft.setSelected(true);
            mTvRight.setSelected(false);
            mLeftSelected = true;
            //改变activity UI显示 ===》控件的存在依赖于activity的业务逻辑
            //接口回调
            if (mListener != null) {
                mListener.onCheck(mTvLeft, true);
            }
        } else if (v == mTvRight) {
            //如果右侧没有被选中,才能被选中
            if (!mLeftSelected) {
                return;
            }
            //点击右侧,右侧选中
            mTvLeft.setSelected(false);
            mTvRight.setSelected(true);
            mLeftSelected = false;

            //改变activity UI显示
            //接口回调
            if (mListener != null) {
                mListener.onCheck(mTvRight, false);
            }
        }
    }

    public void setOnCheckListener(OnCheckListener listener) {
        this.mListener = listener;
    }

    /**
     * 当选中时
     *
     * @param view:选中的view
     * @param leftSelected:是否左侧选中
     */
    public interface OnCheckListener {
        void onCheck(View view, boolean leftSelected);
    }
}
