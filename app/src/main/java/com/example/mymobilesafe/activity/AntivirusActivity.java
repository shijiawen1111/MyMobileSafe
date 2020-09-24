package com.example.mymobilesafe.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.bean.AntivirusBean;
import com.example.mymobilesafe.db.AntivirusDao;
import com.example.mymobilesafe.utils.MD5Utils;
import com.example.mymobilesafe.utils.PackageUtils;
import com.github.lzyzsd.circleprogress.ArcProgress;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JW.S on 2020/9/22 9:28 PM.
 */
public class AntivirusActivity extends Activity implements View.OnClickListener {

    private PackageManager mPm;
    private ListView mAntivirusListView;
    private List<AntivirusBean> mDatas;
    private AntivirusAdapter mAdapter;
    private RelativeLayout mRlContainerScanned;
    private RelativeLayout mRlContainerScanning;
    private ArcProgress mArcProgress;
    private TextView mTvPkg;
    private TextView mTvResult;
    private Button mBtnScan;
    private LinearLayout mLlContainerAnimation;
    private ImageView mIvLeft;
    private ImageView mIvRight;
    private int mVirusCount;
    private boolean isPause;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antivirus);
        mPm = getPackageManager();
        initView();
        initEvent();
        initData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
    }

    private void initView() {
        mAntivirusListView = findViewById(R.id.antivirus_listview);
        mRlContainerScanned = findViewById(R.id.antivirus_rl_container_scanned);
        mRlContainerScanning = findViewById(R.id.antivirus_rl_container_scanning);
        mArcProgress = findViewById(R.id.antivirus_arc_progress);
        mTvPkg = findViewById(R.id.antivirus_tv_pkg);
        mTvResult = findViewById(R.id.antivirus_tv_result);
        mBtnScan = findViewById(R.id.antivirus_btn_scan);
        mLlContainerAnimation = findViewById(R.id.antivirus_ll_container_animation);
        mIvLeft = findViewById(R.id.antivirus_iv_left);
        mIvRight = findViewById(R.id.antivirus_iv_right);

    }

    private void initEvent() {
        mBtnScan.setOnClickListener(this);
    }

    private void initData() {
        mDatas = new ArrayList<>();
        mAdapter = new AntivirusAdapter();
        mAntivirusListView.setAdapter(mAdapter);
        startScan();
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnScan) {
            clickScan();
        }
    }

    private class AntivirusAdapter extends BaseAdapter {

        private AntivirusBean bean;

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
            AntivirusItemViewHolder mHolder = null;
            if (convertView == null) {
                //没有复用
                //1.填充布局
                convertView = View.inflate(AntivirusActivity.this, R.layout.item_antivirus, null);
                //2.新建holder
                mHolder = new AntivirusItemViewHolder();
                //3.设置标签
                convertView.setTag(mHolder);
                //4.初始化标签
                mHolder.ivIcon = convertView.findViewById(R.id.item_anvirus_iv_icon);
                mHolder.ivClear = convertView.findViewById(R.id.item_antivirus_iv_clear);
                mHolder.tvName = convertView.findViewById(R.id.item_antivirus_tv_name);
                mHolder.tvVirus = convertView.findViewById(R.id.item_anvirus_tv_isvirus);
            } else {
                //有复用
                mHolder = (AntivirusItemViewHolder) convertView.getTag();
            }
            //设置数据
            bean = mDatas.get(position);
            if (bean.icon == null) {
                mHolder.ivIcon.setImageResource(R.drawable.ic_default);
            } else {
                mHolder.ivIcon.setImageDrawable(bean.icon);
            }
            mHolder.tvName.setText(bean.name);
            mHolder.tvVirus.setText(bean.isAnvirus ? "病毒" : "安全");
            mHolder.tvVirus.setTextColor(bean.isAnvirus ? Color.RED : Color.GREEN);
            mHolder.tvVirus.setVisibility(bean.isAnvirus ? View.VISIBLE : View.GONE);
            mHolder.ivClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.DELETE");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse("package:" + bean.packageName));
                    startActivity(intent);
                }
            });
            return convertView;
        }

        private class AntivirusItemViewHolder {
            ImageView ivIcon;
            ImageView ivClear;
            TextView tvName;
            TextView tvVirus;
        }
    }

    /**
     * 开始扫描病毒
     */
    private void startScan() {
        new AsyncTask<Void, AntivirusBean, Void>() {
            private int max;
            private int progress;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //清空数据,更新UI
                mDatas.clear();
                mAdapter.notifyDataSetChanged();
                mVirusCount = 0;
                //显示扫描中,隐藏扫描后
                mRlContainerScanned.setVisibility(View.GONE);
                mRlContainerScanning.setVisibility(View.VISIBLE);
                mLlContainerAnimation.setVisibility(View.INVISIBLE);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                List<PackageInfo> packages = mPm.getInstalledPackages(0);
                for (PackageInfo info : packages) {
                    if (isPause) {
                        break;
                    }
                    String apkPath = info.applicationInfo.sourceDir;
                    File file = new File(apkPath);
                    String md5 = MD5Utils.md5(file);
                    AntivirusBean bean = new AntivirusBean();
                    bean.icon = PackageUtils.getAppIcon(AntivirusActivity.this, info);
                    bean.name = PackageUtils.getAppName(AntivirusActivity.this, info);
                    //判断一个应用是否是病毒
                    bean.isAnvirus = AntivirusDao.isVirus(AntivirusActivity.this, md5);
                    bean.packageName = info.packageName;
                    //推送到主线程
                    publishProgress(bean);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(AntivirusBean... values) {
                super.onProgressUpdate(values);
                AntivirusBean bean = values[0];
                //边添加边UI更新
                if (bean.isAnvirus) {
                    //是病毒
                    mDatas.add(0, bean);
                    mVirusCount++;
                } else {
                    mDatas.add(bean);
                }
                mAdapter.notifyDataSetChanged();
                mAntivirusListView.smoothScrollToPosition(mAdapter.getCount());

                //显示扫描的包名
                mTvPkg.setText(bean.packageName);
                //显示进度
                int perent = (int) ((++progress) * 100f / max + 0.5f);
                mArcProgress.setProgress(progress);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //显示扫描后,隐藏扫描中
                mRlContainerScanned.setVisibility(View.VISIBLE);
                mRlContainerScanning.setVisibility(View.GONE);
                mLlContainerAnimation.setVisibility(View.VISIBLE);
                //滚动到顶部
                mAntivirusListView.smoothScrollToPosition(0);
                //显示病毒结果
                String text;
                if (mVirusCount == 0) {
                    text = "你的手机很安全";
                } else {
                    text = "你的手机有 " + mVirusCount + " 个病毒,请你杀死!";
                }
                mTvResult.setText(text);
                //获取扫描中容器显示的图片,拆成两块,分别给动画view去做动画
                mRlContainerScanning.setDrawingCacheEnabled(true);
                mRlContainerScanning.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                Bitmap bitmap = mRlContainerScanning.getDrawingCache();
                Bitmap leftBitmap = getLeftBitmap(bitmap);
                mIvLeft.setImageBitmap(leftBitmap);
                Bitmap rightBitmap = getRightBitmap(bitmap);
                mIvRight.setImageBitmap(rightBitmap);
                //要显示扫描后打开的动画
                startOpenAnimation();
            }
        }.execute();
    }

    private Bitmap getLeftBitmap(Bitmap bitmap) {
        //多媒体知识
        //canvas
        int width = (int) (bitmap.getWidth() / 2f + 0.5f);
        int height = bitmap.getHeight();
        Bitmap copyBitmap = Bitmap.createBitmap(width, height, bitmap.getConfig());

        //准备canvas
        Canvas canvas = new Canvas(copyBitmap);
        //准备矩阵
        Matrix matrx = new Matrix();
        //准备画笔
        Paint paint = new Paint();
        //画图
        canvas.drawBitmap(bitmap, matrx, paint);
        return copyBitmap;
    }

    private Bitmap getRightBitmap(Bitmap bitmap) {
        //多媒体知识
        int width = (int) (bitmap.getWidth() / 2f + 0.5f);
        int height = bitmap.getHeight();
        Bitmap copyBitmap = Bitmap.createBitmap(width, height, bitmap.getConfig());
        Canvas canvas = new Canvas(copyBitmap);
        //准备矩阵
        Matrix matrix = new Matrix();
        //准备画笔
        Paint paint = new Paint();
        //画图
        canvas.drawBitmap(bitmap, matrix, paint);
        return copyBitmap;
    }

    private void startOpenAnimation() {
        int leftWidth = mIvLeft.getWidth();
        int rightWidth = mIvRight.getWidth();

        //让左侧图片向左做位移动画,透明度动画
        //让右侧图片向右做位移动画,透明度动画
        //让扫描结束后容器做透明度动画
        //动画集合
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(mIvLeft, "translationX", 0, -leftWidth),
                ObjectAnimator.ofFloat(mIvLeft, "alpha", 1f, 0),
                ObjectAnimator.ofFloat(mIvRight, "translationX", 0, rightWidth),
                ObjectAnimator.ofFloat(mIvRight, "alpha", 1f, 0),
                ObjectAnimator.ofFloat(mRlContainerScanned, "alpha", 0, 1f));
        set.setDuration(5000);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //让重新扫描的按钮不可用
                mBtnScan.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //让重新扫描的按钮可用
                mBtnScan.setEnabled(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        //开启动画
        set.start();
    }

    /**
     * 点击重新扫描
     */
    private void clickScan() {
        startCloseAnimation();
    }

    private void startCloseAnimation() {
        int leftWidth = mIvLeft.getWidth();
        int rightWidth = mIvRight.getWidth();
        //让左侧的图片向左做位移动画,透明度动画
        //让右侧的图拍向右做位移动画,透明度动画
        //让扫描结束后的容器做透明度动画
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(mIvLeft, "translationX", -leftWidth, 0),
                ObjectAnimator.ofFloat(mIvLeft, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(mIvRight, "translationX", rightWidth, 0),
                ObjectAnimator.ofFloat(mIvRight, "alpha", 0, 1f),
                ObjectAnimator.ofFloat(mRlContainerScanned, "alpha", 1f, 0f));
        set.setDuration(5000);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                // 让重新扫描的按钮不可用
                mBtnScan.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                startScan();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();
    }
}
