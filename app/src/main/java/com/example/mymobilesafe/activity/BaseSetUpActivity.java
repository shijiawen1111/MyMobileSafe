package com.example.mymobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.mymobilesafe.R;

/**
 * Created by JW.S on 2020/5/22 10:05 AM.
 */
public abstract class BaseSetUpActivity extends Activity {
    private static final String TAG = "BaseSetUpActivity";
    private GestureDetector detector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        1.创建手势识别器
        detector = new GestureDetector(BaseSetUpActivity.this, new GestureDetector.SimpleOnGestureListener() {

            // 2.实现对应监听中关心操作
            // onFling()触摸滑动的回调的方法
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                // e1:手势开始时的点
                // e2:手势结束时的点
                // velocityX :水平方向的速率
                // velocityY :垂直方向的速率
                float x1 = e1.getRawX();//开始点的横坐标
                float y1 = e1.getRawY();//开始点的纵坐标
                float x2 = e2.getRawX();//结束点的横坐标
                float y2 = e2.getRawY();//结束点的纵坐标
                if (Math.abs(velocityX) < 200) {//取水平方向速率的绝对值,若小于200则直接返回.
                    Log.d(TAG, "velocityX: " + velocityX);
                    return true;
                }
//                垂直方向上判断
                if (Math.abs(y2 - y1) > Math.abs(x2 - x1)) {//取终点的纵坐标减去起点的纵坐标的绝对会判断是否大于终点的横坐标减去起点的横坐标的绝对值,条件成立则返回.
                    // Y轴运动
                    Log.d(TAG, "Y轴运动");
                    return true;
                }
                if (x1 > x2) {
                    // 如果从右向左滑动，下一步的操作
                    Log.d(TAG, "手势执行下一步的操作");
                    doNext();
                } else {
                    // 如果从左向右滑动，上一步的操作
                    Log.d(TAG, "手势执行上一步的操作");
                    doPre();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // 3. 手势识别器去捕获分析touch行为
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void doNext() {
        if (performNext()) {
            Log.d(TAG, "终端下一步的操作");
            return;
        }
        int enterAnim = R.anim.next_enter;  //前进的activity进入对应的动画
        int exitAnim = R.anim.next_exit;    //前进的activity结束对应的动画
        overridePendingTransition(enterAnim, exitAnim);
        finish();
    }

    private void doPre() {
        // 执行上一步操作
        if (performPre()) {
            Log.d(TAG, "中断上一步的操作");
            return;
        }
        // ### 共同行为 ####
        // 设置过渡动画
        int enterAnim = R.anim.pre_enter;//返回的activity进入对应的动画
        int exitAnim = R.anim.pre_exit;  //返回的activity结束对应的动画
        overridePendingTransition(enterAnim, exitAnim);
        // ### 共同行为 ####
        // 结束页面
        finish();
    }

    public void clickPre(View view) {
        doPre();
//        performPre();
//        //设置过度动画
//        int enterAnim = R.anim.pre_enter;       //后退的activity进入动画
//        int exitAnim = R.anim.pre_exit;         //后退的activity退出动画
//        overridePendingTransition(enterAnim, exitAnim);
//        finish();
    }

    public void clickNext(View view) {
        doNext();
//        performNext();
//        //设置过度动画
//        int enterAnim = R.anim.next_enter;      //前进的activity进入动画
//        int exitAnim = R.anim.next_exit;        //前进的activity退出动画
//        overridePendingTransition(enterAnim, exitAnim);
//        finish();
    }

    /**
     * 上一步的操作
     * @return true:不往下执行了 fasle:继续往下执行
     */
    protected abstract boolean performPre();

    /**
     * 下一步的操作
     * @return true:不往下执行了 fasle:继续往下执行
     */
    protected abstract boolean performNext();
}
