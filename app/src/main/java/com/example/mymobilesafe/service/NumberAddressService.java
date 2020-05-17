package com.example.mymobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.mymobilesafe.db.NumberAddressDao;
import com.example.mymobilesafe.view.NumberAddressToast;

public class NumberAddressService extends Service {

    private TelephonyManager mTm;
    private NumberAddressToast mToast;
    private static final String TAG = "NumberAddressService";
    private CallInListener mCallInlistener;
    private CallOutReceiver mCallOutReceiver;

    public NumberAddressService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "号码归属地服务的开启");
        mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mToast = new NumberAddressToast(NumberAddressService.this);
        // 1.如果电话拨入时，显示拨入号码的归属地
        mCallInlistener = new CallInListener();
        mTm.listen(mCallInlistener, PhoneStateListener.LISTEN_CALL_STATE);

        // 2.如果电话拨出时，显示拨出号码的归属地
        // 广播接受者获取对应拨出状态,动态注册
        mCallOutReceiver = new CallOutReceiver();
        IntentFilter filter = new IntentFilter();
        // 接收的行为
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(mCallOutReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "号码归属地服务的关闭");
        // 注销拨入的监听
        mTm.listen(mCallInlistener,PhoneStateListener.LISTEN_NONE);
        // 注销拨出的广播接受者
        unregisterReceiver(mCallOutReceiver);
    }

    private class CallInListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            super.onCallStateChanged(state, phoneNumber);
            // * @see TelephonyManager#CALL_STATE_IDLE
            // * @see TelephonyManager#CALL_STATE_RINGING
            // * @see TelephonyManager#CALL_STATE_OFFHOOK
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                // 响铃时显示归属地
                String address = NumberAddressDao.findAddress(NumberAddressService.this, phoneNumber);
                // // 显示 Toast替代
                // Toast.makeText(NumberAddressService.this, address,
                // Toast.LENGTH_LONG).show();
                mToast.show(address);
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
//                隐藏Toast
                mToast.hide();
            }
        }
    }

    private class CallOutReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            // 响铃时显示归属地
            String address = NumberAddressDao.findAddress(NumberAddressService.this, number);
            // 显示 Toast替代 TODO:
            // Toast.makeText(NumberAddressService.this, address,
            // Toast.LENGTH_LONG).show();
            // Toast toast = Toast.makeText(NumberAddressService.this, address,
            // Toast.LENGTH_LONG);
            // toast.show();
            mToast.show(address);
        }
    }
}
