package com.example.mymobilesafe.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.text.format.Formatter;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.mymobilesafe.bean.ProcessBean;
import com.example.mymobilesafe.business.ProcessProvider;

import java.util.List;

/**
 * Created by JW.S on 2020/9/4 11:19 PM.
 */
public class KillProcessService extends IntentService {
    private Handler handler = new Handler();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public KillProcessService(String name) {
        super("KillProcessService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //1.在子线程中执行的(可以执行耗时的操作)
        //2.多次同时调用服务,onHandleIntent会排队执行,等所有的方法调用完成后,服务就销毁
        //3.单词开启服务,onHandleIntent执行完成后,服务就销毁
        int beforeCount = ProcessProvider.getRunningProcessCount(this);
        long beforeMemory = ProcessProvider.getUsedMemory(this);
        //杀死进程
        List<ProcessBean> process = ProcessProvider.getProcess(this);
        for (ProcessBean bean : process) {
            String packageName = bean.pkg.packageName;
            if (packageName.equals(getPackageName())){
                continue;
            }
            ProcessProvider.killProcess(this, packageName);
        }
        int afterCount = ProcessProvider.getRunningProcessCount(this);
        long afterMemory = ProcessProvider.getUsedMemory(this);
        final int count = beforeCount - afterCount;//杀死的进程数
        if (count > 0){
            final long memory = beforeMemory - afterMemory;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(KillProcessService.this, "杀死了"+count+"进程,节省内存"+ Formatter.formatFileSize(KillProcessService.this, memory), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(KillProcessService.this, "没有可以优化的", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
