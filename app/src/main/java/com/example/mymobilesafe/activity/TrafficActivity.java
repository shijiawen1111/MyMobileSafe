package com.example.mymobilesafe.activity;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.bean.TraficBean;
import com.example.mymobilesafe.business.TraficProvider;

import java.util.List;

public class TrafficActivity extends Activity {

    private ListView mListView;
    private LinearLayout mLoadingView;
    private List<TraficBean> mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        mListView = findViewById(R.id.trafic_listview);
        mLoadingView = findViewById(R.id.include_ll_loading);
    }

    private void initEvent() {

    }

    private void initData() {
        // 假数据
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // 进度显示
                mLoadingView.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                mDatas = TraficProvider.getTrafics(TrafficActivity.this);
                System.out.println("datas:" + mDatas.toString());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                // 进度隐藏
                mLoadingView.setVisibility(View.GONE);
                // 设置adapter
                mListView.setAdapter(new TraficAdatper());
            }

        }.execute();
    }

    private class TraficAdatper extends BaseAdapter {
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
            ItemViewHolder holder = null;
            if (convertView == null) {
                //没有复用
                //1.加载布局
                //2.创建holder
                //3.设置标签
                //4.查找子View
                convertView = View.inflate(TrafficActivity.this, R.layout.item_trafic, null);
                holder = new ItemViewHolder();
                convertView.setTag(holder);
                holder.icon = convertView.findViewById(R.id.item_trafic_iv_icon);
                holder.name = convertView.findViewById(R.id.item_trafic_tv_name);
                holder.receive = convertView.findViewById(R.id.item_trafic_tv_receive);
                holder.send = convertView.findViewById(R.id.item_trafic_tv_send);
            } else {
                //有复用
                holder = (ItemViewHolder) convertView.getTag();
            }

            //设置数据
            TraficBean bean = mDatas.get(position);
            if (bean.icon != null) {
                holder.icon.setImageResource(R.drawable.ic_default);
            } else {
                holder.icon.setImageDrawable(bean.icon);
            }
            holder.name.setText(bean.name);
            holder.receive.setText("接受:" + Formatter.formatFileSize(TrafficActivity.this, bean.receive));
            holder.send.setText("发送:" + Formatter.formatFileSize(TrafficActivity.this, bean.send));
            return convertView;
        }

        private class ItemViewHolder {
            ImageView icon;
            TextView name;
            TextView receive;
            TextView send;
        }
    }
}
