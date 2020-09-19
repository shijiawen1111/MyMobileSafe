package com.example.mymobilesafe.business;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.example.mymobilesafe.bean.TraficBean;
import com.example.mymobilesafe.utils.PackageUtils;
import com.example.mymobilesafe.utils.StreamUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JW.S on 2020/9/5 11:32 PM.
 */
public class TraficProvider {
    /**
     * 获得流量信息
     *
     * @param context
     * @return
     */
    public static List<TraficBean> getTrafics(Context context) {
        List<TraficBean> list = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        // 获得所有应用程序的发送和接收的情况
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo info : packages) {
            Drawable icon = PackageUtils.getAppIcon(context, info);
            String name = PackageUtils.getAppName(context, info);
            int uid = info.applicationInfo.uid;
            long receive = getReceive(uid);
            long send = getSend(uid);
            TraficBean bean = new TraficBean();
            bean.icon = icon;
            bean.name = name;
            bean.receive = receive;
            bean.send = send;
            list.add(bean);
        }
        return list;
    }

    /**
     * proc/uid_stat/xxxx/tcp_rcv -->接收的数据
     *
     * @param uid
     * @return
     */
    private static long getReceive(int uid) {
        String path = "/proc/uid_stat/" + uid + "/tcp_rcv";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(path)));
            String readLine = reader.readLine();
            return Long.valueOf(readLine);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtils.closeIO(reader);
        }
        return 0;
    }

    /**
     * roc/uid_stat/xxxx/tcp_snd --> 发送的数据
     * @param uid
     * @return
     */
    private static long getSend(int uid) {
        String path = "/proc/uid_stat/" + uid + "/tcp_snd";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(path)));
            String readLine = reader.readLine();
            return Long.valueOf(readLine);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtils.closeIO(reader);
        }
        return 0;
    }
}
