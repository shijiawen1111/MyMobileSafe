package com.example.mymobilesafe.service;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import com.example.mymobilesafe.activity.LockActivity;
import com.example.mymobilesafe.db.AppLockDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JW.S on 2020/9/17 10:28 AM.
 */
public class AppLockService extends AccessibilityService {

    private AppLockDao mDao;
    private AppLockReceiver mReceiver;
    private List<String> mUnLockApps;

    @Override
    public void onCreate() {
        super.onCreate();
        mDao = new AppLockDao(this);
        mUnLockApps = new ArrayList<>();
        //注册广播
        mReceiver = new AppLockReceiver();
        IntentFilter mFilter = new IntentFilter();
        mFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        mFilter.addAction("com.example.mymobilesafe");
        mFilter.addAction(Intent.ACTION_SCREEN_OFF);//锁屏
        registerReceiver(mReceiver, mFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    /**
     * 可选实现,系统会在成功连接上你的服务的时候调用这个方法，
     * 在这个方法里你可以做一下初始化工作，例如设备的声音震动管理，
     * 也可以调用setServiceInfo()进行配置工作
     */
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    /**
     * 必须实现,通过这个函数可以接收系统发送来的AccessibilityEvent，
     * 接收来的AccessibilityEvent是经过过滤的，过滤是在配置工作时设置的。
     * @param event
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        String className = event.getClassName().toString();
        String packageName = event.getPackageName().toString();
        //判断是否不需要拦截
        if (mUnLockApps.contains(packageName)) {
            return;
        }
        //判断是否切换到上锁的应用
        if (mDao.findIsLock(packageName)) {
            //找到上锁的
            //弹出提示
            //Toast.makeText(this, "提示上锁", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LockActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("pkg", packageName);
            startActivity(intent);
        }
    }

    /**
     * 这个在系统想要中断AccessibilityService返给的响应时会调用。
     * 在整个生命周期里会被调用多次
     */
    @Override
    public void onInterrupt() {

    }

    /**
     * 可选实现,在系统将要关闭这个AccessibilityService会被调用。
     * 在这个方法中进行一些释放资源的工作
     * @param intent
     * @return
     */
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private class AppLockReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("com.example.mymobilesafe".equals(action)) {
                //接受到放行拦截的应用
                String packageName = intent.getStringExtra("pkg");
                mUnLockApps.add(packageName);
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                //清空所有放行的应用
                mUnLockApps.clear();
            }
        }
    }
}
