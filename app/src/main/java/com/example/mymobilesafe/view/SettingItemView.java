package com.example.mymobilesafe.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mymobilesafe.R;

public class SettingItemView extends RelativeLayout {
    private static final int BACKGROUND_START = 0;
    private static final int BACKGROUND_MIDDLE = 1;
    private static final int BACKROUND_END = 2;
    private TextView mTvText;
    private ImageView mIvToggle;

    public SettingItemView(Context context) {
        super(context);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        挂在xml
        View.inflate(context, R.layout.view_setting_item, this);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SettingItemView);
        String text = ta.getString(R.styleable.SettingItemView_sivText);
//        背景
        int background = ta.getInt(R.styleable.SettingItemView_sivBackground, BACKGROUND_START);
        boolean toggleEnable = ta.getBoolean(R.styleable.SettingItemView_sivToggleEnable, true);

        ta.recycle();

        mTvText = findViewById(R.id.view_setting_tv_text);
        mIvToggle = findViewById(R.id.view_setting_iv_toggle);
//        赋值
        mTvText.setText(text);

        switch (background) {
            case BACKGROUND_START:
                this.setBackgroundResource(R.drawable.item_first_selector);
                break;
            case BACKGROUND_MIDDLE:
                this.setBackgroundResource(R.drawable.item_middle_seletor);
                break;
            case BACKROUND_END:
                this.setBackgroundResource(R.drawable.item_last_selector);
                break;
            default:
                break;
        }
//        图片开关
        mIvToggle.setVisibility(toggleEnable ? View.VISIBLE : View.GONE);
    }

    //    设置开关的状态
    public void setToggleState(boolean open) {
        mIvToggle.setImageResource(open ? R.drawable.on : R.drawable.off);
    }
}
