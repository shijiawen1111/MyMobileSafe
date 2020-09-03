package com.example.mymobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.bean.PkgBean;
import com.example.mymobilesafe.bean.ProcessBean;
import com.example.mymobilesafe.business.ProcessProvider;
import com.example.mymobilesafe.view.ProgressStateView;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class ProcessManagerActivity extends Activity implements View.OnClickListener {
    private ImageView pm_iv_clean;
    private ProgressStateView pm_psv_process;
    private ProgressStateView pm_psv_rom;
    private ListView pm_listview;
    private Button pm_btn_reverse_choose_all;
    private Button pm_btn_choose_all;
    private View include_ll_loading;
    private int mRunningProcessCount;
    private int mTotalProcessCount;
    private long mUsedMemory;
    private long mTotalMemory;
    private List<ProcessBean> mDatas;
    private HashSet mPkgs;
    private ProcessAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);
        initView();
        initEvent();
        initData();
    }


    private void initView() {
        pm_iv_clean = findViewById(R.id.pm_iv_clean);
        pm_psv_process = findViewById(R.id.pm_psv_process);
        pm_psv_rom = findViewById(R.id.pm_psv_rom);
        pm_listview = findViewById(R.id.pm_listview);
        pm_btn_reverse_choose_all = findViewById(R.id.pm_btn_reverse_choose_all);
        pm_btn_choose_all = findViewById(R.id.pm_btn_choose_all);
        include_ll_loading = findViewById(R.id.include_ll_loading);
    }

    private void initEvent() {
        pm_iv_clean.setOnClickListener(this);
    }

    private void initData() {
        // 1.设置进程数据
        mRunningProcessCount = ProcessProvider.getRunningProcessCount(ProcessManagerActivity.this);
        mTotalProcessCount = ProcessProvider.getTotalProcessCount(ProcessManagerActivity.this);
        loadProcess();

        // 2. 已经使用了的内存（当前运行的进程销毁的内存总和）,手机总内存是多大
        mUsedMemory = ProcessProvider.getUsedMemory(ProcessManagerActivity.this);
        mTotalMemory = ProcessProvider.getTotalMemory();
        loadMemory();
        loadListView();
    }

    private void loadListView() {
        include_ll_loading.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //获取数据
                mDatas = ProcessProvider.getProcess(ProcessManagerActivity.this);
                //进行排序,根据应用的名称
                Collections.sort(mDatas, new Comparator<ProcessBean>() {
                    @Override
                    public int compare(ProcessBean lhs, ProcessBean rhs) {
                        String lFirstLetter = lhs.pkg.firstLetter;
                        String rFirstLetter = rhs.pkg.firstLetter;
                        return lFirstLetter.compareToIgnoreCase(rFirstLetter);
                    }
                });
                //初始化记录所有应用程序
                mPkgs = new HashSet<PkgBean>();
                for (ProcessBean bean : mDatas) {
                    PkgBean pkg = bean.pkg;
                    mPkgs.add(pkg);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        include_ll_loading.setVisibility(View.GONE);
                        //给listview设置adapter
                        mAdapter = new ProcessAdapter();
                        pm_listview.setAdapter(mAdapter);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        clickClean();
    }

    /**
     * 杀死选中的应用进程
     */
    private void clickClean() {
        if (mDatas.size() == 0 || mDatas == null) {
            return;
        }

        long releaseMemory = 0;
        int killCount = 0;
        Map<Integer, Long> map = new HashMap<>();
        Iterator<PkgBean> iterator = mPkgs.iterator();
        while (iterator.hasNext()) {
            PkgBean pkg = iterator.next();
            //找到选中的应用
            if (pkg.isChecked) {
                //杀死应用
                ProcessProvider.killProcess(ProcessManagerActivity.this, pkg.packageName);
                //内存移除
                iterator.remove();
                ListIterator<ProcessBean> listIterator = mDatas.listIterator();
                while (listIterator.hasNext()) {
                    ProcessBean bean = listIterator.next();
                    if (bean.pkg.packageName.equals(pkg.packageName)) {
                        //移除
                        listIterator.remove();
                        //获得进程的内存
                        long memory = bean.memory;
                        map.put(bean.pid, memory);
                    }
                }
            }
        }
        //计算杀死的个数和释放的内存
        for (Map.Entry<Integer, Long> me : map.entrySet()) {
            releaseMemory += me.getValue();
        }
        killCount = map.size();
        //更新iu
        mAdapter.notifyDataSetChanged();
        if (killCount == 0) {
            Toast.makeText(this, "没有可杀死的进程!", Toast.LENGTH_SHORT).show();
        } else {
            String text = "杀死了" + killCount + "个进程,释放了" + Formatter.formatFileSize(ProcessManagerActivity.this, releaseMemory) + "内存!";
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        }
        //ui顶部改变
        mRunningProcessCount -= killCount;
        loadProcess();
        mUsedMemory -= releaseMemory;
        loadMemory();
    }

    private class ProcessAdapter extends BaseAdapter {
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
            if (mDatas != null) {
                return position;
            }
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemViewHolder holder = null;
            if (convertView == null) {
                //没有复用
                convertView = View.inflate(ProcessManagerActivity.this, R.layout.item_process, null);
                holder = new ItemViewHolder();
                convertView.setTag(holder);
                holder.tvProcessName = convertView.findViewById(R.id.item_process_tv_name);
                holder.tvProcessSize = convertView.findViewById(R.id.item_process_tv_size);
            } else {
                //有复用
                holder = (ItemViewHolder) convertView.getTag();
            }

            ProcessBean bean = mDatas.get(position);
            holder.tvProcessName.setText("进程名:" + bean.processName);
            holder.tvProcessSize.setText("占用空间:" + Formatter.formatFileSize(ProcessManagerActivity.this, bean.memory));
            return convertView;
        }

        private class ItemViewHolder {
            TextView tvProcessName;
            TextView tvProcessSize;
        }
    }

    private void loadProcess() {
        int processProgress = (int) (mRunningProcessCount * 100f / mTotalProcessCount + 0.5f);
        pm_psv_process.setLeftText("正在运行:" + mRunningProcessCount + "个");
        pm_psv_process.setRightText("可有进程:" + mTotalProcessCount + "个");
        pm_psv_process.setMaxProgress(processProgress);
    }

    private void loadMemory() {
        long freeMemory = mTotalMemory - mUsedMemory;
        int memoryProgress = (int) (mUsedMemory * 100f / mTotalMemory + 0.5f);
        pm_psv_rom.setLeftText("占用内存:" + Formatter.formatFileSize(ProcessManagerActivity.this, mUsedMemory));
        pm_psv_rom.setRightText("剩余内存:" + Formatter.formatFileSize(ProcessManagerActivity.this, freeMemory));
        pm_psv_rom.setMaxProgress(memoryProgress);
    }
}
