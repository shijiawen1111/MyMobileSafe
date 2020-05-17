package com.example.mymobilesafe.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.io.File;

public class PackageUtils {
    /**
     * 获得版本名称
     *
     * @param mContext
     * @return
     */
    public static String getVersionName(Context mContext) {
        try {
            PackageManager packageManager = mContext.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
            String versionName = packageInfo.versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得版本号
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int versionCode = packageInfo.versionCode;
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取应用名称
     * @param context
     * @param info
     * @return
     */
    public static String getAppName(Context context, PackageInfo info) {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo applicationInfo = info.applicationInfo;
        String str = applicationInfo.loadLabel(pm).toString();
        return str;
    }

    /**
     * 获取应用图标
     * @param context
     * @param info
     * @return
     */
    public static Drawable getAppIcon(Context context, PackageInfo info) {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo applicationInfo = info.applicationInfo;
        Drawable loadIcon = applicationInfo.loadIcon(pm);
        return loadIcon;
    }

    /**
     * 获取应用所占用的空间大小
     * @param info
     * @return
     */
    public static long getAppSpace(PackageInfo info) {
        ApplicationInfo applicationInfo = info.applicationInfo;
        String sourceDir = applicationInfo.sourceDir;
        File file = new File(sourceDir);
        long length = file.length();
        return length;
    }
}
