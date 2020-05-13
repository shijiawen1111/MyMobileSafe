package com.example.mymobilesafe.view;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

/**
 * 走马灯的实现类
 */
public class MarqueeTextView extends androidx.appcompat.widget.AppCompatTextView {
    public MarqueeTextView(Context context) {
        super(context);

    }

    public MarqueeTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //        android:singleLine="true"
        setSingleLine();
        //        android:ellipsize="marquee"
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        //        android:focusable="true"
        setFocusable(true);
        //        android:focusableInTouchMode="true"
        setFocusableInTouchMode(true);
        //        android:marqueeRepeatLimit="marquee_forever"
        setMarqueeRepeatLimit(-1);
    }

    /**
     * 若有焦点,则直接返回,不走父类方法
     * @return
     */
    @Override
    public boolean isFocused() {
        return true;
    }

    /**
     * 当焦点被改变回调的方法
     * @param focused
     * @param direction
     * @param previouslyFocusedRect
     */
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if (focused) {
//            有焦点时走父类方法,焦点被抢走时,不去操作
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
        }
    }

    /**
     * 当窗体焦点被改变回调的方法
     * @param hasWindowFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (hasWindowFocus) {
//            窗体有焦点时走父类的方法,窗体失去焦点,不去操作
            super.onWindowFocusChanged(hasWindowFocus);
        }
    }
}
