package com.example.mymobilesafe.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.utils.Config;
import com.example.mymobilesafe.utils.PreferenceUtils;

public class NumberAddressToast implements OnTouchListener {
	private static final String TAG = "NumberAddressToast";
	// mWM.addView(mView, mParams);

	private WindowManager mWM;
	private View mView;
	private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
	private float mDownX;

	private float mDownY;

	private Context mContext;

	public NumberAddressToast(Context context) {
		this.mContext = context;
		// 初始化WindowManager
		mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

		// 加载自定义Toast的样式
		mView = View.inflate(context, R.layout.toast_number_address, null);

		// param初始化
		final WindowManager.LayoutParams params = mParams;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
		// | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		// params.windowAnimations =
		// com.android.internal.R.style.Animation_Toast;
		// params.type = WindowManager.LayoutParams.TYPE_TOAST;// 不可以被触摸的
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;// 可以被触摸的,可以显示在打电话界面上方的
		params.setTitle("Toast");

		// 设置touch监听
		mView.setOnTouchListener(this);
	}

	public void show(String address) {
		if (mView.getParent() != null) {
			mWM.removeView(mView);
		}
		// 设置样式
		int style = PreferenceUtils.getInt(mContext, Config.KEY_ADDRESS_STYLE,
				-1);
		if (style == -1) {
			// 一个都没有选中时
			style = R.drawable.toast_normal;
		}

		mView.setBackgroundResource(style);

		TextView tv = (TextView) mView.findViewById(R.id.toast_tv_location);
		tv.setText(address);

		mWM.addView(mView, mParams);
	}

	public void hide() {
		if (mView.getParent() != null) {
			mWM.removeView(mView);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.d(TAG, "触摸了自定义的toast");

		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mDownX = event.getRawX();
			mDownY = event.getRawY();

			break;
		case MotionEvent.ACTION_MOVE:
			float moveX = event.getRawX();
			float moveY = event.getRawY();

			float diffX = moveX - mDownX;
			float diffY = moveY - mDownY;

			mParams.x += diffX;
			mParams.y += diffY;

			mWM.updateViewLayout(mView, mParams);

			mDownX = moveX;
			mDownY = moveY;
			break;
		case MotionEvent.ACTION_UP:
			break;
		default:
			break;
		}

		return true;
	}
}
