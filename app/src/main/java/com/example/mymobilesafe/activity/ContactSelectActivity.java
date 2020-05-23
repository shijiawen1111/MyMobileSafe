package com.example.mymobilesafe.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.bean.ContactBean;
import com.example.mymobilesafe.business.ContactProvider;

import java.util.List;

public class ContactSelectActivity extends Activity implements AdapterView.OnItemClickListener {

    public static final String KEY_NUMBER = "number";
    private ListView mListView;
    private List<ContactBean> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_select);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        mListView = findViewById(R.id.cs_listview);
    }

    private void initData() {

        mData = ContactProvider.getAllContacts(this);

        mListView.setAdapter(new ContactSelectAdapter());
    }

    private void initEvent() {
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 获得点击的号码,将号码返回给打开的页面
        // 传数据回去的地方
        ContactBean bean = mData.get(position);
        Intent data = new Intent();
        data.putExtra(KEY_NUMBER, bean.number);
//        把数据返回给开启的界面
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    private class ContactSelectAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (mData != null) {
                return mData.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mData != null) {
                return mData.get(position);
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
//                如果没有复用
                // 1.加载布局
                convertView = View.inflate(ContactSelectActivity.this, R.layout.item_contact, null);
                // 2.新建holder
                holder = new ViewHolder();
                // 3.给convertView设置标记
                convertView.setTag(holder);
                // 4. 给holder中的view赋值(findViewById)
                holder.ivIcon = convertView.findViewById(R.id.item_contact_iv_icon);
                holder.tvName = convertView.findViewById(R.id.item_contact_tv_name);
                holder.tvNumber = convertView.findViewById(R.id.item_contact_tv_number);
            } else {
//                有复用
                holder = (ViewHolder) convertView.getTag();
            }

            // 给view设置数据
            ContactBean bean = mData.get(position);
            holder.ivIcon.setImageBitmap(ContactProvider.getContactPhoto(ContactSelectActivity.this, bean.contactId));
            holder.tvName.setText(bean.name);
            holder.tvNumber.setText(bean.number);

            return convertView;
        }
    }

    private class ViewHolder {
        // itemview的持有者
        ImageView ivIcon;
        TextView tvName;
        TextView tvNumber;
    }
}
