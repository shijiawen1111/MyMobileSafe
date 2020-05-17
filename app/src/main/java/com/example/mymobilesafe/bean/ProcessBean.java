package com.example.mymobilesafe.bean;

/**
 * Created by JW.S on 2020/5/14 7:03 PM.
 */
public class ProcessBean {
    public String processName;      //进程名字
    public long memory;             //进程占用的内存
    public PkgBean pkg;             //进程对面的应用程序
    public int  pid;                //进程对应的id
}
