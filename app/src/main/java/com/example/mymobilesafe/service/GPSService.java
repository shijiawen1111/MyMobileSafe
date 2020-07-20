package com.example.mymobilesafe.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.mymobilesafe.utils.Config;
import com.example.mymobilesafe.utils.GPSUtils;
import com.example.mymobilesafe.utils.PreferenceUtils;

/**
 * Created by JW.S on 2020/7/20 10:52 PM.
 */
public class GPSService extends Service {

    private LocationManager mLocationManager;
    private GPSListener mListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //获得手机的经纬度
        //1.获得位置管理者
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //2.注册位置监听
        mListener = new GPSListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) mListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //4.注销维护监听
            mLocationManager.removeUpdates(mListener);
        //5.注册权限
    }
    //实现listener
    private class GPSListener implements LocationListener{
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();//获得纬度
            double longitude = location.getLongitude();//获得经度
            //拿到纬度和经度
            //1.转换成经度
            double[] result = GPSUtils.parse(GPSService.this, latitude, longitude);
            //2.将转换后的经纬度发送给安全号码
            SmsManager smsManager = SmsManager.getDefault();
            String number = PreferenceUtils.getString(GPSService.this, Config.KEY_SJFD_NUMBER);
            String text = "longitude:"+result[0] + " latidude:" +result[1];
            smsManager.sendTextMessage(number, null, text, null, null);
            //3.停止服务
            stopSelf();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
