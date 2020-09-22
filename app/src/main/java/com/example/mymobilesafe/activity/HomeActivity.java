package com.example.mymobilesafe.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.bean.HomeBean;
import com.example.mymobilesafe.utils.Config;
import com.example.mymobilesafe.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends Activity implements AdapterView.OnItemClickListener {

    private static final String TAG = "HomeActivity";
    private ImageView mIvLogo;
    private GridView mGvGridView;

    private final int[] ICONS = new int[]{
            R.drawable.sjfd, R.drawable.srlj, R.drawable.rjgj,
            R.drawable.jcgl, R.drawable.lltj, R.drawable.sjsd, R.drawable.hcql, R.drawable.cygj
    };
    private final String[] TITLES = new String[]{
            "手机防盗", "骚扰拦截", "软件管家",
            "进程管理", "流量统计", "手机杀毒", "缓存清理", "常用工具"
    };
    private final String[] DESCS = new String[]{
            "远程定位手机", "全面拦截骚扰", "管理您的软件",
            "管理运行进程", "流量一目了然", "病毒无处藏身", "系统快如火箭", "工具大全"
    };
    private List mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        初始化控件
        initView();
//        让logo进行动画
        doLogoAnimation();
//        给gridView加载数据
        initGridViewData();
//        初始化事件
        initEvent();
    }

    private void initGridViewData() {
//        给list数据初始化
        mDatas = new ArrayList<HomeBean>();
        for (int i = 0; i < ICONS.length; i++) {
            HomeBean bean = new HomeBean();
            bean.icon = ICONS[i];
            bean.title = TITLES[i];
            bean.desc = DESCS[i];
            mDatas.add(bean);
        }
        mGvGridView.setAdapter(new HomeAdapter());
    }

    private void initEvent() {
        mGvGridView.setOnItemClickListener(this);
    }

    private void initView() {
        mIvLogo = findViewById(R.id.home_iv_logo);
        mGvGridView = findViewById(R.id.home_gridview);
    }

    private void doLogoAnimation() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mIvLogo, "rotationY", 0, 90, 180, 270, 360, 180, 270);
        animator.setDuration(3000);//设置动画时长
        animator.setRepeatCount(ObjectAnimator.INFINITE);//设置可重复
        animator.setRepeatMode(ObjectAnimator.REVERSE);//设置重复模式
        animator.start();
    }

    public void onClickSetting(View view) {
        Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
//                    "手机防盗"
                clickSjfd();
                break;
            case 1:
//                    "骚扰拦截"
                clickSrlj();
                break;
            case 2:
//                    "软件管家"
                clickRjgj();
                break;
            case 3:
//                    "进程管理"
                clickJcgl();
                break;
            case 4:
//                    "流量统计"
                clickLltj();
                break;
            case 5:
//                    "手机杀毒"

                break;
            case 6:
//                    "缓存清理"
                clickHcql();
                break;
            case 7:
//                    "常用工具"
                clickCygj();
                break;
            default:
                break;
        }
    }

    /**
     * 点击缓存清理
     */
    private void clickHcql() {
        Intent intent = new Intent(HomeActivity.this, CacheClearActivity.class);
        startActivity(intent);
    }

    /**
     * 点击流量统计
     */
    private void clickLltj() {
        Intent intent = new Intent(HomeActivity.this, TrafficActivity.class);
        startActivity(intent);
    }

    /**
     * 点击进程管理
     */
    private void clickJcgl() {
        Intent intent = new Intent(HomeActivity.this, ProcessManagerActivity.class);
        startActivity(intent);
    }

    /**
     * 点击软件管家
     */
    private void clickRjgj() {
        Intent intent = new Intent(HomeActivity.this, AppManagerActivity.class);
        startActivity(intent);
    }

    /**
     * 点击骚扰拦截
     */
    private void clickSrlj() {
        //TODO
        Intent intent = new Intent(HomeActivity.this, CallSmsSafeActivity.class);
        startActivity(intent);
    }

    /**
     * 点击手机防盗
     */
    private void clickSjfd() {
        //去存储中获得存储的密码
        String pwd = PreferenceUtils.getString(this, Config.KEY_SJFD_PWD);
        //是否设置过密码
        if (!TextUtils.isEmpty(pwd)) {
//            设置过,则显示密码输入的对话框
            showEnterPwdDialog();
        } else {
//            未设置过,则显示密码设置的对话框
            showPwdSettingDialog();
        }
    }

    /**
     * 点击常用工具
     */
    private void clickCygj() {
        Intent intent = new Intent(HomeActivity.this, CommonToolActivity.class);
        startActivity(intent);
    }

    private void showPwdSettingDialog() {
        Log.d(TAG, "显示密码设置的对话框");
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        View view = View.inflate(HomeActivity.this, R.layout.dialog_pwd_setting, null);
        final EditText dialogEtPwd = view.findViewById(R.id.dialog_et_pwd);
        final EditText dialogEtConfirm = view.findViewById(R.id.dialog_et_confirm);
        final Button dialogBtnOk = view.findViewById(R.id.dialog_btn_ok);
        Button dialogBtnCancer = view.findViewById(R.id.dialog_btn_cancel);

        builder.setView(view);
        final AlertDialog dialog = builder.show();
        dialogBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = dialogEtPwd.getText().toString().trim();
//                判断非空
                if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(HomeActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    dialogBtnOk.requestFocus();// 重新请求焦点
                    return;
                }
                String pwdConfirm = dialogEtConfirm.getText().toString().trim();
//                判断非空
                if (TextUtils.isEmpty(pwdConfirm)) {
                    Toast.makeText(HomeActivity.this, "请输入正确密码", Toast.LENGTH_SHORT).show();
                    dialogEtConfirm.requestFocus();
                    return;
                }
//                判断两次输入的密码是否一致
                if (!pwd.equals(pwdConfirm)) {
                    Toast.makeText(HomeActivity.this, "两次密码输入不一致,请重新输入", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 存储密码
                PreferenceUtils.setString(HomeActivity.this, Config.KEY_SJFD_PWD, pwd.trim());
                // 进入设置向导界面
                enterSetup1();
                // 关闭dialog
                dialog.dismiss();
            }
        });
        dialogBtnCancer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //        设置过,则显示密码输入的对话框
    private void showEnterPwdDialog() {
        Log.d(TAG, "显示密码输入的对话框");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        设置自定义的布局
        View view = View.inflate(this, R.layout.dialog_pwd_enter, null);
        final EditText dialogEtPwd = view.findViewById(R.id.dialog_et_pwd);
        Button dialogBtnOk = view.findViewById(R.id.dialog_btn_ok);
        Button dialogBtnCancer = view.findViewById(R.id.dialog_btn_cancel);
        //TODO
        builder.setView(view);
        final AlertDialog dialog = builder.show();//显示
        dialogBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. 密码校验
                String pwd = dialogEtPwd.getText().toString().trim();
                // 非空判断
                if (TextUtils.isEmpty(pwd)) {
                    // 提示用户要输入密码
                    Toast.makeText(HomeActivity.this, "请输入密码:", Toast.LENGTH_SHORT).show();
                    // 设置光标
                    dialogEtPwd.requestFocus();//重新请求焦点
                    return;
                }
                // 密码是否正确
                String savePwd = PreferenceUtils.getString(HomeActivity.this, Config.KEY_SJFD_PWD);
                if (savePwd.equals(pwd)) {
                    // 相同
                    // 通过持久化存储去存储状态值
                    boolean flag = PreferenceUtils.getBoolean(HomeActivity.this, Config.KEY_SJFD_SETUP);
                    // dialog消失
                    dialog.dismiss();
                    if (flag) {
//                        进入设置完成的页面
                        Intent intent = new Intent(HomeActivity.this, SjfdActivity.class);
                        startActivity(intent);
                    } else {
//                        进入设置向导页面
                        enterSetup1();
                    }
                } else {
//                    不同
                    Toast.makeText(HomeActivity.this, "密码不准确,请重新输入!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialogBtnCancer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void enterSetup1() {
        Intent intent = new Intent(HomeActivity.this, SetupActivity1.class);
        startActivity(intent);
    }

    private class HomeAdapter extends BaseAdapter {
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
            convertView = View.inflate(HomeActivity.this, R.layout.item_home, null);
            ImageView ivIcon = convertView.findViewById(R.id.item_home_iv_icon);
            TextView tvTitle = convertView.findViewById(R.id.item_home_tv_title);
            TextView tvDesc = convertView.findViewById(R.id.item_home_tv_desc);
//            给控件设置数据
            HomeBean bean = (HomeBean) mDatas.get(position);
            ivIcon.setImageResource(bean.icon);
            tvTitle.setText(bean.title);
            tvDesc.setText(bean.desc);
            return convertView;
        }
    }
}
