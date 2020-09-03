package com.example.mymobilesafe.business;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.os.Debug;

import com.example.mymobilesafe.bean.PkgBean;
import com.example.mymobilesafe.bean.ProcessBean;
import com.example.mymobilesafe.utils.ChineseToEnglishUtils;
import com.example.mymobilesafe.utils.PackageUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by JW.S on 2020/5/14 10:25 AM.
 */
public class ProcessProvider {
    /**
     * 获得运行的进程数
     *
     * @param context
     * @return
     */
    public static int getRunningProcessCount(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        拿到正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        return processInfos.size();
    }

    /**
     * 统计可有的进程数
     *
     * @param context
     * @return
     */
    public static int getTotalProcessCount(Context context) {
        Set<String> set = new HashSet<>();
        PackageManager manager = context.getPackageManager();
        List<PackageInfo> infos = manager.getInstalledPackages(0);
        for (PackageInfo info : infos) {
            // application,activity,service,receiver,provider进程名
//        相同的值同意一次
//            application
            ApplicationInfo applicationInfo = info.applicationInfo;
            String processName = applicationInfo.processName;
            set.add(processName);

//            activity
            ActivityInfo[] activities = info.activities;
            if (activities != null) {
                for (ActivityInfo activityInfo : activities) {
                    set.add(activityInfo.processName);
                }
            }

//            service
            ServiceInfo[] services = info.services;
            if (services != null) {
                for (ServiceInfo serviceInfo : services) {
                    set.add(serviceInfo.processName);
                }
            }

//            receiver
            ActivityInfo[] receivers = info.receivers;
            if (receivers != null) {
                for (ActivityInfo activityInfo : receivers) {
                    set.add(activityInfo.processName);
                }
            }
//            provider
            ProviderInfo[] providers = info.providers;
            if (providers != null) {
                for (ProviderInfo providerInfo : providers) {
                    set.add(providerInfo.processName);
                }
            }
        }
        return set.size();
    }

    /**
     * 获得使用的内存信息
     *
     * @param context
     * @return
     */
    public static long getUsedMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        拿到正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        int total = 0;
        for (ActivityManager.RunningAppProcessInfo info : infos) {
//            进程id
            int id = info.pid;
            Debug.MemoryInfo memoryInfo = am.getProcessMemoryInfo(new int[]{id})[0];
            int totalPss = memoryInfo.getTotalPss();
            total += totalPss;
        }
        return total;
    }

    public static long getTotalMemory() {
        try {
            File file = new File("/proc/meminfo");
            BufferedReader mReader = new BufferedReader(new FileReader(file));
            String readLine = mReader.readLine();
            readLine = readLine.replace("MemTotal:", "");
            readLine = readLine.replace("kB", "");
            readLine = readLine.trim();
            long result = Long.parseLong(readLine);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //TODO
    public static List<ProcessBean> getProcess(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ProcessBean> datas = new ArrayList<>();
//        获得正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
//        用来记录描述一个进程对应多个应用
        Map<String, List<ProcessBean>> map = new HashMap<>();
        for (ActivityManager.RunningAppProcessInfo info : processes) {
            String processName = info.processName;      //进程名称
            int pid = info.pid;                         //进程id
//            进程占用的内存信息
            Debug.MemoryInfo memoryInfo = am.getProcessMemoryInfo(new int[]{pid})[0];
            int memory = memoryInfo.getTotalPss() * 1024;
            String[] pkgList = info.pkgList;
            for (String pck : pkgList) {
                ProcessBean bean = new ProcessBean();
                bean.processName = processName;
                bean.memory = memory;
                bean.pid = pid;
                List<ProcessBean> lists = map.get(pck);
                if (lists == null) {
                    lists = new ArrayList<ProcessBean>();
                    map.put(pck, lists);
                }
                lists.add(bean);
            }
        }

        PackageManager pm = context.getPackageManager();
        //循环数据
        for (Map.Entry<String, List<ProcessBean>> me : map.entrySet()) {
            try {
                String packageName = me.getKey();
                List<ProcessBean> list = me.getValue();
                PackageInfo info = pm.getPackageInfo(packageName, 0);
                PkgBean pkg = new PkgBean();
                pkg.icon = PackageUtils.getAppIcon(context, info);
                pkg.name = PackageUtils.getAppName(context, info);
                pkg.packageName = packageName;
                pkg.firstLetter = ChineseToEnglishUtils.getPinYinHeadChar(pkg.name).substring(0, 1);
                for (ProcessBean bean : list) {
                    bean.pkg = pkg;
                }
                datas.addAll(list);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return datas;
    }

    /**
     * 杀死应用的进程
     *
     * @param context
     * @param packageName
     */
    public static void killProcess(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(packageName);
    }
}
