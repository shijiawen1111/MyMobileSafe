package com.example.mymobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.bean.BlackBean;
import com.example.mymobilesafe.db.BlackDao;

import java.util.List;

public class CallSmsSafeActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener, AbsListView.OnScrollListener {
    private static final int REQUEST_ADD = 1;
    private static final int REQUEST_UPDATE = 0;
    private static final String TAG = "CallSmsSafeActivity";
    private ImageView mCssIvAdd;
    private ListView mListView;
    private BlackDao mDao;
    private List<BlackBean> mDatas;
    private CallSmsAdapter mAdapter;
    private LinearLayout mLlLoading;
    private ImageView mIvEmpty;
    private int mPages = 10;
    private boolean isLoadingMore = true;//    表示当前正在加载更多,就不往下执行
    private boolean isLoadAll = false;//        表示是否已经是最多了

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
        mLlLoading = findViewById(R.id.css_ll_loading);
        mIvEmpty = findViewById(R.id.css_iv_empty);
    }

    private void initEvent() {
        mCssIvAdd.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);
    }

    private void initData() {

        // 在耗时操作前，显示加载的进度
        mLlLoading.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 查询所有数据,有的时候数据太大,会消耗很多的内存,显得很卡顿,因此我们就改成了分页查询
                //mDatas = mDao.findAll();
                //分页查询,每次查询的数据为20条
                mDatas = mDao.findPart(mPages, 0);
                // 模拟延时操作
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // UI操作，需要回归主线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 耗时操作后，隐藏进度
                        mLlLoading.setVisibility(View.GONE);
                        // 实现adapter--》List<数据>-->item条目
                        mAdapter = new CallSmsAdapter();
                        mListView.setAdapter(mAdapter);
                    }
                });
            }
        }).start();
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

    /**
     * 当ListView滑动状态改变时回调的方法.
     * @param view
     * @param scrollState
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    /**
     * 当ListView滑动时回调的方法
     * @param view
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // firstVisibleItem:第一个可见的条目的position
        // visibleItemCount：可见条目的数量
        // totalItemCount:总共的条目数量
        if (mAdapter == null || mDao == null) {
            return;
        }
        // 获得最后一个可见的条目
        int lastListViewPosition = mListView.getLastVisiblePosition();
        // 必须滑动到底部
        if (lastListViewPosition == mAdapter.getCount() - 1) {
            // 滑动到底部
            // 当前正在加载更多，就不往下执行
            if (isLoadingMore) {
                return;
            }
            // 是否已经是最多了
            if (isLoadAll) {
                return;
            }
            // 开始加载更多了
            Log.d(TAG, "开始加载更多了");
            isLoadingMore = true;
            mLlLoading.setVisibility(View.VISIBLE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 加载更多的数据
                    final List<BlackBean> part = mDao.findPart(mPages, mAdapter.getCount());
                    if (part == null || part.size() < mPages) {
                        isLoadAll = true;
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLlLoading.setVisibility(View.GONE);
                            // 添加部分
                            mDatas.addAll(part);
                            // UI更新
                            mAdapter.notifyDataSetChanged();
                            // 加载更多结束
                            isLoadingMore = false;
                        }
                    });
                }
            }).start();
        }
    }

    private class CallSmsAdapter extends BaseAdapter {

        private BlackBean bean;

        @Override
        public int getCount() {
            if (mDatas != null) {
                // 提示的空view
                mIvEmpty.setVisibility(mDatas.size() == 0 ? View.VISIBLE : View.GONE);
                return mDatas.size();
            }
            // 提示的空view可见
            mListView.setVisibility(View.VISIBLE);
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
