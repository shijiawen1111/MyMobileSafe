package com.example.mymobilesafe.utils;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;

import java.util.List;

/**
 * Created by JW.S on 2020/5/14 9:54 AM.
 */
public class ServiceStateUtils {
    public static boolean isServiceRunning(Context context, Class<? extends Service> clazz) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo serviceInfo : runningServices) {
            String serviceName = serviceInfo.service.getClassName();
            if (clazz.getName().equals(serviceName)) {
//                正在运行的服务
                return true;
            }
        }
        return false;
    }
}
