package com.example.mymobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by JW.S on 2020/8/18 10:07 PM.
 * App信息类
 */
public class AppInfo implements Comparable<AppInfo>{

    /**
     * Drawable不仅仅是表示图片和资源文件,表示的范围更广泛。
     * Bitmap只表示图片资源
     */
    public Drawable icon;   //App图标
    public String name;     //App名称
    public boolean isInstallSD; //是否安装在SD卡
    public long space;      //App的大小
    public boolean isSystem;//是否是系统已用
    public String pakagerName;//包名称

    @Override
    public int compareTo(AppInfo o) {
        boolean lhsSystem = this.isSystem;          //系统应用
        boolean rhsSystem = o.isSystem;             //应用应用
        int lhsInt = lhsSystem ? 1 : 0;             //系统应用所占用空间大于等于1,则返回1;小于1,则返回0.
        int rhsInt = rhsSystem ? 1 : 0;             //用户应用所占用空间大于等于1,则返回1;小于1,则放回0.
        return lhsInt - rhsInt;                     //小于0左边在前面
    }
}
