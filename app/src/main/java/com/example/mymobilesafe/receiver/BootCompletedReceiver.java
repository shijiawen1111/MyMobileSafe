package com.example.mymobilesafe.receiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.mymobilesafe.utils.Config;
import com.example.mymobilesafe.utils.PreferenceUtils;

/**
 * Created by JW.S on 2020/7/18 7:44 PM.
 */
public class BootCompletedReceiver extends BroadcastReceiver {
    private static final String TAG = "BootCompletedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "手机重启了... ");
        // 1. 判断是否开启防盗保护
        boolean protecting = PreferenceUtils.getBoolean(context, Config.KEY_SJFD_PROTECTING);
        // 1)没有开启手机防盗保护
        if (!protecting) {
            Log.d(TAG, "没有开启手机防盗保护...");
            return;
        }
        Log.d(TAG, "开启了手机防盗保护...");
        // 2. 比对sim卡
        // 1)存储的sim卡
        String sim = PreferenceUtils.getString(context, Config.KEY_SJFD_SIM);
        // 2）当前手机的sim卡
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //模拟丢失
        String currentSim = tm.getSimSerialNumber() + "xxx";
        if (sim.equals(currentSim)){
            //存储和当前是一致的,手机没有丢失
            return;
        }
        //手机丢失了
        Log.d(TAG, "发送报警短信...");
        //3. 给安全号码发送报警短信
        Log.d(TAG, "给安全号码发送报警短信...");
        String number = PreferenceUtils.getString(context, Config.KEY_SJFD_NUMBER);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, "mobile is lost!!! sos", null, null);
    }
}
