package com.example.mymobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.example.mymobilesafe.bean.ProcessBean;
import com.example.mymobilesafe.business.ProcessProvider;

import java.util.List;

public class AutoCleanService extends Service {

    private ScreenOffReceiver mReceiver;
    private static final String TAG = "AutoCleanService";

    public AutoCleanService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "自动清理服务开启");
        // 锁屏时自动清理服务
        mReceiver = new ScreenOffReceiver();
        IntentFilter filter = new IntentFilter();
        // 监听锁屏
        filter.addAction(Intent.ACTION_SCREEN_OFF);//锁屏的行为
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        // 注册广播
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "自动清理服务关闭");
        unregisterReceiver(mReceiver);
    }

    private class ScreenOffReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            接收到锁频,杀死进程
            List<ProcessBean> process = ProcessProvider.getProcess(context);
            for (ProcessBean bean : process) {
                if (bean.pkg.packageName.equals(context.getPackageName())) {
//                    不杀死自己
                    return;
                }
                ProcessProvider.killProcess(context, bean.pkg.packageName);
            }
        }
    }
}
