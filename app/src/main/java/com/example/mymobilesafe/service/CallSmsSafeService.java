package com.example.mymobilesafe.service;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.android.internal.telephony.ITelephony;
import com.example.mymobilesafe.bean.BlackBean;
import com.example.mymobilesafe.db.BlackDao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CallSmsSafeService extends Service {
    private static final String TAG = "CallSmsSafeService";
    private BlackDao mDao;
    private SmsReceiver mReceiver;
    private TelephonyManager mTm;
    private CallListerner mCall;

    public CallSmsSafeService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "开启骚扰拦截的服务");
        mDao = new BlackDao(CallSmsSafeService.this);
        // 1. 短信的拦截
        mReceiver = new SmsReceiver();
        // <intent-filter android:priority="1000" >
        // <!-- 接收短信的 -->
        // <action android:name="android.provider.Telephony.SMS_RECEIVED" />
        // </intent-filter>
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mReceiver, filter);

        // 2. 电话拦截
        // 1) 知道电话什么时候拨入
        // 2) 通过代码去挂电话
        mTm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        mCall = new CallListerner();
        mTm.listen(mCall, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "关闭骚扰拦截的服务");
        // 注销短信接受者
        unregisterReceiver(mReceiver);
        // 注销电话的监听
        mTm.listen(mCall, PhoneStateListener.LISTEN_NONE);
    }

    private class SmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 获得短信，发送者的号码,判断号码是否是需要拦截
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            for (Object object : objs) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
//                发送者
                String sender = sms.getOriginatingAddress();
                int type = mDao.findType(sender);
                if (type == BlackBean.TYPE_SMS || type == BlackBean.TYPE_ALL) {
                    // 需要拦截短信,不让用户接收短信
                    Log.d(TAG, "拦截" + sender + "发送的短信");
                    abortBroadcast();
                }
            }
        }
    }

    private class CallListerner extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, final String phoneNumber) {
            super.onCallStateChanged(state, phoneNumber);
            // * @see TelephonyManager#CALL_STATE_IDLE:闲置状态
            // * @see TelephonyManager#CALL_STATE_RINGING:响铃状态
            // * @see TelephonyManager#CALL_STATE_OFFHOOK:摘机状态，接听电话状态
            // state
            // incomingNumber：拨入的号码
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                // 响铃时，正在拨入时
                int type = mDao.findType(phoneNumber);
                // 判断是否是被拦截的电话
                if (type == BlackBean.TYPE_CALL || type == BlackBean.TYPE_ALL) {
                    // 通过代码去挂断电话
                    Log.d(TAG, "通过代码去挂断" + phoneNumber + "电话");
                    // 1) ITelephony 实例
                    // a. ITelephony.aidl添加到代码中--》ITelephony.java
                    ITelephony iTelephony = null;

                    // TelephonyManager.getITelephony()-->ITelephony
                    // 通过反射调用TelephonyManager.getITelephony()--->不可行的
                    // try {
                    // Method method = TelephonyManager.class
                    // .getDeclaredMethod("getITelephony");
                    // iTelephony = (ITelephony) method.invoke(mTm);
                    // } catch (Exception e) {
                    // e.printStackTrace();
                    // }

                    // ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));

                    // IBinder ibinder = ServiceManager
                    // .getService(Context.TELEPHONY_SERVICE);
                    try {
                        // 获取到ServiceManager 的类名
                        Class<?> clazz = Class.forName("android.os.ServiceManager");
                        // 暴力反射（第一个参数：表示方法名字，第二个参数：表示方法的类型）
                        Method method = clazz.getMethod("getService", String.class);
                        // 调用当前的方法（第一个参数：表示谁调用当前方法)
                        IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
                        iTelephony = ITelephony.Stub.asInterface(iBinder);

                        // 2) ITelephony.endCall();
                        iTelephony.endCall();           //异步操作

                        // // 通过线程睡一会
                        // Thread.sleep(300);

                        // 删除通话记录
                        final ContentResolver resolver = getContentResolver();
                        final Uri contentUri = CallLog.Calls.CONTENT_URI;
                        // content://calls
                        // content://calls/1/100
                        // notifyForDescendents: true
                        // -->url下面的任何分支发生改变，都会通知对应的observer
                        // fasle-->只通知当前的url
                        resolver.registerContentObserver(contentUri, true, new ContentObserver(new Handler()) {
                            @Override
                            public void onChange(boolean selfChange) {
                                super.onChange(selfChange);
                                String where = CallLog.Calls.NUMBER + "=?";
                                String[] selectionArgs = {phoneNumber};
                                if (ActivityCompat.checkSelfPermission(CallSmsSafeService.this, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                resolver.delete(contentUri, where, selectionArgs);
                                resolver.unregisterContentObserver(this);
                            }
                        });
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
