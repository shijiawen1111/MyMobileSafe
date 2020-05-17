package com.example.mymobilesafe.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.bean.AddressStyleBean;
import com.example.mymobilesafe.utils.Config;
import com.example.mymobilesafe.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JW.S on 2020/5/17 7:36 PM.
 */
public class AddressStyleDialog extends Dialog implements AdapterView.OnItemClickListener {
    private static final String TAG = "AddressStyleDialog";
    private final int mScreenWidth;
    private static final String[] titiles = new String[]{
            "半透明", "活力橙", "卫士蓝",
            "金属灰", "苹果绿"
    };

    private static final int[] styles = new int[]{R.drawable.toast_normal,
            R.drawable.toast_orange, R.drawable.toast_blue,
            R.drawable.toast_gray, R.drawable.toast_green};
    private ListView mListView;
    private List<AddressStyleBean> mDatas;

    public AddressStyleDialog(@NonNull Context context) {
        super(context);
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        mScreenWidth = display.widthPixels;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉默认的titlebar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_address_style);  //设置dialog的布局

        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM | Gravity.CENTER;
        params.width = mScreenWidth;
        window.setAttributes(params);

        initView();
        initDate();
        initEvent();

    }

    private void initEvent() {
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 点击条目时的事件
        // dialog销毁
        dismiss();
        AddressStyleBean bean = mDatas.get(position);
        int style = bean.style;
        // 持久化存储选中项
        PreferenceUtils.setInt(getContext(), Config.KEY_ADDRESS_STYLE, style);
    }

    private void initDate() {
        int style = PreferenceUtils.getInt(getContext(), Config.KEY_ADDRESS_STYLE, -1);
        Log.d(TAG, "initDate: ");
        mDatas = new ArrayList();
        for (int i = 0; i < titiles.length; i++) {
            AddressStyleBean bean = new AddressStyleBean();
            bean.style = styles[i];
            bean.title = titiles[i];
//            一个也没选中
            if (style == -1) {
                style = styles[0];
            }
            bean.selected = style == styles[i] ? true : false;
            mDatas.add(bean);
        }
        mListView.setAdapter(new AddressStyleAdapter());
    }

    private void initView() {
        mListView = findViewById(R.id.dialog_listview);
    }

    private class AddressStyleAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (mDatas != null) {
                return mDatas.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mDatas != null) {
                return mDatas.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                // 没有复用
                // 1。加载
                convertView = View.inflate(getContext(), R.layout.item_address_style, null);
                // 2. 新建holder
                holder = new ViewHolder();
                // 3.tag
                convertView.setTag(holder);
                // 4.findviewbyid
                holder.ivStyle = convertView.findViewById(R.id.item_address_iv_style);
                holder.ivSelected = convertView.findViewById(R.id.item_address_iv_selected);
                holder.tvTitle = convertView.findViewById(R.id.item_address_tv_title);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // 设置数据
            AddressStyleBean bean = mDatas.get(position);
            holder.ivStyle.setImageResource(bean.style);
            holder.ivSelected.setVisibility(bean.selected ? View.VISIBLE : View.GONE);
            holder.tvTitle.setText(bean.title);

            return convertView;
        }

        private class ViewHolder {
            ImageView ivStyle;
            TextView tvTitle;
            ImageView ivSelected;
        }
    }
}
