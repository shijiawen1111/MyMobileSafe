package com.example.mymobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;
import androidx.annotation.Nullable;
import com.example.mymobilesafe.R;
import com.example.mymobilesafe.business.ProcessProvider;
import com.example.mymobilesafe.receiver.ProcessWidgetProvider;

/**
 * Created by JW.S on 2020/9/4 12:32 AM.
 */
public class ProcessService extends Service {

    private boolean isRunning;
    private ScreenReceiver mReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //在锁屏时不要去更新ui,解锁屏幕时需要更新
        mReceiver = new ScreenReceiver();
        IntentFilter mFilter = new IntentFilter();
        mFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        mFilter.addAction(Intent.ACTION_SCREEN_OFF);//锁频
        mFilter.addAction(Intent.ACTION_SCREEN_ON);//解锁屏幕
        registerReceiver(mReceiver, mFilter);
        start();
    }

    private void start() {
        isRunning = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning){
                    // 15分钟
                    ComponentName provider = new ComponentName(ProcessService.this, ProcessWidgetProvider.class);
                    RemoteViews views = new RemoteViews(ProcessService.this.getPackageName(), R.layout.process_widget);
                    // 1.显示进程数
                    int runningProcessCount = ProcessProvider.getRunningProcessCount(ProcessService.this);
                    views.setTextViewText(R.id.process_count, "正在运行的进程数:"+runningProcessCount);
                    // 2.显示可用的内存
                    long freeMemory = ProcessProvider.getTotalMemory() - ProcessProvider.getUsedMemory(ProcessService.this);
                    views.setTextViewText(R.id.process_memory,"可用内存:"+ Formatter.formatFileSize(ProcessService.this, freeMemory));
                    // 3.点击事件
                    // 延期意图:打开activity，发送广播，开启服务
                    Intent intent = new Intent(ProcessService.this, KillProcessService.class);
                    PendingIntent pendingIntent = PendingIntent.getService(ProcessService.this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    views.setOnClickPendingIntent(R.id.btn_clear,pendingIntent);
                    //每隔2S更新widget
                    AppWidgetManager.getInstance(ProcessService.this).updateAppWidget(provider, views);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        unregisterReceiver(mReceiver);
    }

    private class ScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 判断是解锁还是锁屏
            String action = intent.getAction();
            if(Intent.ACTION_SCREEN_OFF.equals(action)){
                //锁频
                //停止更新
                isRunning = false;
            }else if (Intent.ACTION_SCREEN_ON.equals(action)){
                //解锁
                //开启更新
                start();
            }
        }
    }
}
