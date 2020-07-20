package com.example.mymobilesafe.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by JW.S on 2020/7/20 11:11 PM.
 */
public class GPSUtils {
    /**
     * @param context   上下文
     * @param latitude  GPS的纬度
     * @param longitude GPS的经度
     * @return 返回值
     */
    public static double[] parse(Context context, double latitude, double longitude) {
        AssetManager assets = context.getAssets();//拿到资源目录
        InputStream inputStream = null;
        try {
            inputStream = assets.open("axisoffset.dat");
            ModifyOffset instance = ModifyOffset.getInstance(inputStream);

            ModifyOffset.PointDouble pt = new ModifyOffset.PointDouble(longitude, latitude);
            ModifyOffset.PointDouble result = instance.s2c(pt);
            return new double[]{result.x, result.y};
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StreamUtils.closeIO(inputStream);
        }
        return null;
    }
}
