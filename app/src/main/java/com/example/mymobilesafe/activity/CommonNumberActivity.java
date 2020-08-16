package com.example.mymobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.bean.ChildBean;
import com.example.mymobilesafe.bean.GroupBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommonNumberActivity extends Activity {
    private ExpandableListView mEdListView;
    private List<GroupBean> mGroupDatas;
    private GroupBean mGroupBean;
    private int mCurrentOpenGroup = -1;//当前group控制条目,表示一个都没有展开

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_number);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        mEdListView = findViewById(R.id.eb_listview);
    }

    private void initData() {
        Random rdm = new Random();
        // 创建假数据
        mGroupDatas = new ArrayList<>();
        for (int i = 0; i < rdm.nextInt(50); i++) {
            mGroupBean = new GroupBean();
            mGroupBean.title = "group-" + i;
            mGroupBean.childrenDatas = new ArrayList<ChildBean>();
            for (int j = 0; j < rdm.nextInt(100); j++) {
                ChildBean childBean = new ChildBean();
                childBean.name = i + "-" + j + "的名称";
                childBean.number = i + "-" + j + "的号码";
                mGroupBean.childrenDatas.add(childBean);
            }
            mGroupDatas.add(mGroupBean);
        }
        //真实数据的创建
        //mGroupDatas = CommonNumberDao.getGroupDatas(CommonNumberActivity.this);
        // adapter --> List<Group数据>--->Group数据(必备List<Child数据>)
        mEdListView.setAdapter(new CommonNumberAdapter());
    }

    private void initEvent() {

        //点击child条目的时候拨打电话
        mEdListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ChildBean childBean = mGroupDatas.get(groupPosition).childrenDatas.get(childPosition);
                String number = childBean.number;
                // 拨号
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + number));
                startActivity(intent);
                return false;
            }
        });

        //点击group控制展开与关闭
        mEdListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // 如果点击的当前条目是关闭的，就去展开，要求置顶显示
                if (mCurrentOpenGroup != groupPosition) {
                    //展开
                    mEdListView.expandGroup(groupPosition);
                    // 关闭已经打开的
                    mEdListView.collapseGroup(mCurrentOpenGroup);
                    // 置顶
                    mEdListView.setSelectedGroup(groupPosition);
                    // 记录当前打开
                    mCurrentOpenGroup = groupPosition;
                } else {
                    // 点击的是打开的，关闭当前
                    mEdListView.collapseGroup(groupPosition);
                    // 记录没有一个是打开的
                    mCurrentOpenGroup = -1;
                }
                // 是否需要系统实现点击行为
                return true;
            }
        });
    }

    private class CommonNumberAdapter extends BaseExpandableListAdapter {
        @Override
        public int getGroupCount() {
            if (mGroupDatas != null) {
                return mGroupDatas.size();
            }
            return 0;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            if (mGroupDatas != null) {
                GroupBean groupBean = mGroupDatas.get(groupPosition);
                if (groupBean != null) {
                    List<ChildBean> childrenDatas = groupBean.childrenDatas;
                    if (childrenDatas != null) {
                        return childrenDatas.size();
                    }
                }
            }
            return 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            if (mGroupDatas != null) {
                return mGroupDatas.get(groupPosition);
            }
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            if (mGroupDatas != null) {
                GroupBean groupBean = mGroupDatas.get(groupPosition);
                if (groupBean != null) {
                    List<ChildBean> childrenDatas = groupBean.childrenDatas;
                    if (childrenDatas != null) {
                        return childrenDatas.get(childPosition);
                    }
                }
            }
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupViewHoler holder = null;
            if (convertView == null) {
                //没有复用
                // 1. 加载布局
                convertView = View.inflate(CommonNumberActivity.this, R.layout.item_group, null);
                // 2. 新建holder
                holder = new GroupViewHoler();
                // 3. 设置标记
                convertView.setTag(holder);
                // 4. findViewById
                holder.tvTitle = convertView.findViewById(R.id.item_group_tv_title);
            } else {
                // 有复用
                holder = (GroupViewHoler) convertView.getTag();
            }
            // 设置数据
            GroupBean groupBean = mGroupDatas.get(groupPosition);
            holder.tvTitle.setText(groupBean.title);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChilderViewHolder holder;
            if (convertView == null) {
                //没有复用
                //1.加载布局
                //2.新建holder
                //3.设置标签
                //4.findViewById
                convertView = View.inflate(CommonNumberActivity.this, R.layout.child_item, null);
                holder = new ChilderViewHolder();
                convertView.setTag(holder);
                holder.tvChildName = convertView.findViewById(R.id.child_title_tv_name);
                holder.tvChildNumber = convertView.findViewById(R.id.child_title_tv_number);
            } else {
                //有复用
                holder = (ChilderViewHolder) convertView.getTag();
            }
            //设置数据
            ChildBean childBean = (ChildBean) getChild(groupPosition, childPosition);
            holder.tvChildName.setText(childBean.name);
            holder.tvChildNumber.setText(childBean.number);
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            // 用来控制child条目是否可以被点击
            return true;
        }
    }

    private class GroupViewHoler {
        TextView tvTitle;
    }

    private class ChilderViewHolder {
        TextView tvChildName;
        TextView tvChildNumber;
    }
}

