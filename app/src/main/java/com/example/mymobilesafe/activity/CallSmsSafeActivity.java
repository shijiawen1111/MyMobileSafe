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
import android.widget.Toast;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.bean.BlackBean;
import com.example.mymobilesafe.db.BlackDao;

import java.util.List;

public class CallSmsSafeActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final int REQUEST_ADD = 1;
    private static final int REQUEST_UPDATE = 0;
    private ImageView mCssIvAdd;
    private ListView mListView;
    private BlackDao mDao;
    private List<BlackBean> mDatas;
    private CallSmsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callsmssafe);
        //获取Dao
        mDao = new BlackDao(this);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        mCssIvAdd = findViewById(R.id.css_iv_add);
        mListView = findViewById(R.id.css_listview);
    }

    private void initEvent() {
        mCssIvAdd.setOnClickListener(this);
    }

    private void initData() {
        // 查询所有数据
        mDatas = mDao.findAll();
        // 实现adapter--》List<数据>-->item条目
        mAdapter = new CallSmsAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(CallSmsSafeActivity.this, BlackEditActivity.class);
        intent.setAction(BlackEditActivity.ACTION_ADD);
        startActivityForResult(intent, REQUEST_ADD);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BlackBean bean = mDatas.get(position);
        // listView条目的点击事件
        Intent intent = new Intent(this, BlackEditActivity.class);
        intent.setAction(BlackEditActivity.ACTION_UPDATE);
        intent.putExtra(BlackEditActivity.KEY_TYPE, bean.type);
        intent.putExtra(BlackEditActivity.KEY_NUMBER, bean.number);
        intent.putExtra(BlackEditActivity.KEY_POSITION, position);
        startActivityForResult(intent, REQUEST_UPDATE);
    }

    private class CallSmsAdapter extends BaseAdapter {

        private BlackBean bean;

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
            return position;
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
                // 1. 加载layout
                convertView = View.inflate(CallSmsSafeActivity.this, R.layout.item_callsmssafe, null);
                // 2. 新建holder
                holder = new ViewHolder();
                // 3. 设置标记
                convertView.setTag(holder);
                // 4. 给holder中的view--》 findViewById
                holder.tvNumber = convertView.findViewById(R.id.item_css_tv_number);
                holder.tvType = convertView.findViewById(R.id.item_css_tv_type);
                holder.ivDelete = convertView.findViewById(R.id.item_css_iv_delete);
                holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 数据库删除
                        boolean success = mDao.delete(bean.number);
                        if (success) {
                            // 删除数据(内存移除)
                            mDatas.remove(bean);
                            // 更新adapter
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(CallSmsSafeActivity.this, "删除失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                // 有复用的逻辑
                holder = (ViewHolder) convertView.getTag();
            }
            // 数据
            bean = mDatas.get(position);//做成CallSmsAdapter类的成员变量,在做每一个listview的item删除的时候会被用到
            holder.tvNumber.setText(bean.number);
            switch (bean.type) {
                case BlackBean.TYPE_SMS:
                    holder.tvType.setText("短信拦截!");
                    break;
                case BlackBean.TYPE_CALL:
                    holder.tvType.setText("电话拦截!");
                    break;
                case BlackBean.TYPE_ALL:
                    holder.tvType.setText("电话+短信拦截!");
                    break;
                default:
                    break;
            }
            return convertView;
        }
    }

    private static class ViewHolder {
        TextView tvNumber;
        TextView tvType;
        ImageView ivDelete;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD) {//添加黑名单的逻辑
            // 添加的请求返回的数据
            if (resultCode == Activity.RESULT_OK) {
                // 成功返回数据
                // 取数据
                String number = data.getStringExtra(BlackEditActivity.KEY_NUMBER);
                int type = data.getIntExtra(BlackEditActivity.KEY_TYPE, -1);
                BlackBean bean = new BlackBean();
                bean.number = number;
                bean.type = type;
                // 修改list数据
                mDatas.add(bean);
                // 刷新adapter
                mAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == REQUEST_UPDATE) {//更新黑名单的逻辑
            if (resultCode == Activity.RESULT_OK) {
                int position = data.getIntExtra(BlackEditActivity.KEY_POSITION, -1);
                int type = data.getIntExtra(BlackEditActivity.KEY_TYPE, -1);
                //更新list数据
                BlackBean bean = mDatas.get(position);
                bean.type = type;
                //刷新adapter
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}
