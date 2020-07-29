package com.example.mymobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.service.GPSService;
import com.example.mymobilesafe.utils.Config;
import com.example.mymobilesafe.utils.PreferenceUtils;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //接受短信
        Object[] objs = (Object[]) intent.getExtras().get("pdus");
        for (Object obj : objs) {
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
            //发送者
            String sender = sms.getOriginatingAddress();
            //发送的内容
            String content = sms.getMessageBody();
            String number = PreferenceUtils.getString(context, Config.KEY_SJFD_NUMBER);
            //判断发送者是否是安全号码
            if (sender.equals(number)){
                //若是安全号码发送的,判断内容是否是指令
                if ("#*location*#".equals(content)){
                    Intent service = new Intent(context, GPSService.class);
                    context.startService(service);
                    Log.d(TAG, "GPS追踪");
                }else if ("#*wipedata*#".equals(content)){
                    //TODO
                    Log.d(TAG, "远程消除数据");
                    DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                    //清理内部和外部存储
                    dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                }else if ("#*alarm*#".equals(content)){
                    MediaPlayer player = MediaPlayer.create(context, R.raw.alarm);
                    player.setLooping(true);//无限播放
                    player.setVolume(1f, 1f);//设置声音
                    player.start();
                    Log.d(TAG, "播放报警音乐");
                }else if (!"#*lockscreen*#".equals(content) && content.startsWith("#*lockscreen*#")){
                    //TODO
                    Log.d(TAG, "远程锁屏");
                    DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                    //设置锁屏密码
                    dpm.resetPassword("123", 0);
                    //立即锁屏
                    dpm.lockNow();
                }else if (!"#*lockscreen*#".equals(content)){
                    //TODO
                    Log.d(TAG, "远程锁屏");
                    DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                    String password = content.substring("#*lockscreen*".length());
                    //设置锁屏密码
                    dpm.resetPassword(password, 0);
                    //立即锁屏
                    dpm.lockNow();
                }
                //不让用户看到短信内容
                abortBroadcast();
            }
        }
    }
}
