package com.example.mymobilesafe.business;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.example.mymobilesafe.bean.AppInfo;
import com.example.mymobilesafe.utils.PackageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JW.S on 2020/8/18 10:15 PM.
 */
public class AppProvider {
    /**
     * 获取所有已安装的应用程序
     *
     * @param context
     * @return
     */
    public static List<AppInfo> getAllApps(Context context) {
        List<AppInfo> list = new ArrayList<>();
        //通过上下文获取包管理器
        PackageManager pm = context.getPackageManager();
        //通过包管理器获取已经安装的所有的安装包
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
        //迭代所有的安装包
        for (PackageInfo packageInfo : installedPackages) {
            //获取到应用的包名
            String appPackagerName = PackageUtils.getAppName(context, packageInfo);
            //获取到应用的icon
            Drawable appPackagerIcon = PackageUtils.getAppIcon(context, packageInfo);
            boolean isInstallSD = PackageUtils.isInstallSD(packageInfo);
            AppInfo appInfo = new AppInfo();
            appInfo.icon = appPackagerIcon;
            appInfo.name = appPackagerName;
            appInfo.space = PackageUtils.getAppSpace(packageInfo);
            appInfo.isInstallSD = isInstallSD;
            appInfo.isSystem = PackageUtils.isSystemApp(packageInfo);
            appInfo.pakagerName = packageInfo.packageName;
            list.add(appInfo);
        }
        return list;
    }

    /**
     * 获取所有已经安装并且可以运行的应用程序
     * @param context
     * @return
     */
    public static List<AppInfo> getAllsLuchApps(Context context) {
        List<AppInfo> list = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        //迭代所有的安装包
        for (PackageInfo info : packages) {
            //如果不具备启动条件则继续
            Intent intent = pm.getLaunchIntentForPackage(info.packageName);
            if (intent == null) {
                continue;
            }
            //获取到应用的包名
            String name = PackageUtils.getAppName(context, info);
            //获取到应用的icon
            Drawable icon = PackageUtils.getAppIcon(context, info);
            boolean installSD = PackageUtils.isInstallSD(info);
            AppInfo bean = new AppInfo();
            bean.name = name;
            bean.icon = icon;
            bean.space = PackageUtils.getAppSpace(info);
            bean.isInstallSD = installSD;
            bean.isSystem = PackageUtils.isSystemApp(info);
            bean.pakagerName = info.packageName;
            list.add(bean);
        }
        return list;
    }

    /**
     * 获取所有应用名称
     *
     * @param context
     * @param info
     * @return
     */
    public static String getAllAppName(Context context, PackageInfo info) {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo applicationInfo = info.applicationInfo;
        return applicationInfo.loadLabel(pm).toString();
    }

    /**
     * 获取所有应用icon
     *
     * @param context
     * @param info
     * @return
     */
    public static Drawable getAllAppIcon(Context context, PackageInfo info) {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo applicationInfo = info.applicationInfo;
        return applicationInfo.loadIcon(pm);
    }

    /**
     * 获取所有应用程序apk的大小
     *
     * @param info
     * @return
     */
    public static long getAllAppSpace(PackageInfo info) {
        ApplicationInfo applicationInfo = info.applicationInfo;
        String sourceDir = applicationInfo.sourceDir;
        File file = new File(sourceDir);
        return file.length();
    }

    /**
     * 判断应用是否安装在SD卡上
     *
     * @param info
     * @return
     */
    public static boolean isAppInstallSD(PackageInfo info) {
        ApplicationInfo applicationInfo = info.applicationInfo;
        int flags = applicationInfo.flags;//应用的标记,能力或者特性
        boolean isInstallSD = false;
        if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
            isInstallSD = true;
        }
        return isInstallSD;
    }

    /**
     * 判断应用是否是系统应用
     *
     * @param info
     * @return
     */
    public static boolean isSystemApp(PackageInfo info) {
        ApplicationInfo applicationInfo = info.applicationInfo;
        int flags = applicationInfo.flags;//应用的标记,能力或者特性
        boolean isSystemApp = false;
        if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
            isSystemApp = true;
        }
        return isSystemApp;
    }
}
