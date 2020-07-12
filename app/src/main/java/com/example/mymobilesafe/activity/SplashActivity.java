package com.example.mymobilesafe.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mymobilesafe.R;
import com.example.mymobilesafe.utils.Config;
import com.example.mymobilesafe.utils.PackageUtils;
import com.example.mymobilesafe.utils.PreferenceUtils;
import com.example.mymobilesafe.utils.StreamUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SplashActivity extends Activity {
    private static final int WHAT_SHOW_UPDATE_DIALOG = 100;
    private static final int WHAT_SHOW_UPDATE_TOAST = 101;
    private static final int REQUEST_CODE_INSTALL = 100;
    private TextView splashTvVersion;
    private static final String TAG = "SplashActivity";
    private String mDownLoadUrl;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_SHOW_UPDATE_DIALOG:
                    showSafeUpateDialog((String) msg.obj);
                    break;
                case WHAT_SHOW_UPDATE_TOAST:
                    Toast.makeText(SplashActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    //让用户进入主页面
                    load2Home();
                    break;
                default:
                    break;
            }
        }
    };


    private void showSafeUpateDialog(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);//点击其他区域不可取消dialog
        builder.setTitle("版本更新提醒");
        builder.setMessage(content);//设置提示信息,由服务器提供
        builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                load2Home();//  进入主页面
            }
        });
        builder.setPositiveButton("立刻更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downLoadNewApk();// 去下载新的apk
            }
        });
        builder.show();
    }

    private void downLoadNewApk() {
        // 弹出进度的dialog
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);//设置点击其他区域不可取消dialog
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.show();
        //去网络下载
        new Thread(new downLoadApkTask(dialog)).start();
    }

    /**
     * 去网络上下载apk的任务
     */
    private class downLoadApkTask implements Runnable {
        private ProgressDialog dialog;
        private InputStream inputStream;
        private FileOutputStream fos;

        public downLoadApkTask(ProgressDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void run() {
            try {
                //1.去具体的网络接口去下载apk
                String path = mDownLoadUrl;//怎么来的
                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //2.设置超时
                conn.setConnectTimeout(2 * 1000);
                conn.setReadTimeout(2 * 1000);
                //3.新的apk文件流
                inputStream = conn.getInputStream();
                //4.获得要下载的文件大小
                int contentLength = conn.getContentLength();
                dialog.setMax(contentLength);
                //5.指定输出的apk文件cdcard下
                File file = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".apk");
                //6.写到文件中
                fos = new FileOutputStream(file);
                //7.反复的读写的输入流
                int len = -1;
                byte[] buffers = new byte[1024];
                int progress = 0;
                while ((len = inputStream.read(buffers)) != -1) {
                    fos.write(buffers, 0, len);
                    progress += len;
                    //设置进度条大小
                    dialog.setProgress(progress);
                    Thread.sleep(2 * 1000);
                }
                //8.下载完成,dialog消失
                dialog.dismiss();
                //9.提示安装
                installApk(file);
            } catch (IOException e) {
                e.printStackTrace();
                notifyError("101");
            } catch (InterruptedException e) {
                e.printStackTrace();
                notifyError("103");
            } finally {
                StreamUtils.closeIO(inputStream);
                StreamUtils.closeIO(fos);
            }
        }
    }

    /**
     * 去安装Apk的方法
     *
     * @param file
     */
    private void installApk(File file) {
        //发送意图去安装apk
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivityForResult(intent, REQUEST_CODE_INSTALL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "data: " + data);
        if (requestCode == REQUEST_CODE_INSTALL) {
            //  响应的是安装的请求结果
            if (requestCode == Activity.RESULT_OK) {
                //确定
            } else if (requestCode == Activity.RESULT_CANCELED) {
                //取消
                Log.d(TAG, "用户点击了取消");
                load2Home();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splashTvVersion = findViewById(R.id.splash_tv_version);
        splashTvVersion.setText("版本:" + PackageUtils.getVersionName(this));

        // 创建快捷图标
        createShortcut();

        // 加载归属地数据库
        copyNumberAddressDB();
        copyCommonNumberDB();
        copyAntivirusDB();//TODO

        // 如果设置了自动更新
        boolean flag = PreferenceUtils.getBoolean(this, Config.KEY_AUTO_UPDATE,
                true);
        if (flag) {
            // 版本更新检测,去网络中获取最新的版本
            checkVersionUpdate();
        } else {
            load2Home();
        }
    }

    /**
     * 创建快捷图标
     */
    private void createShortcut() {
        //若存在快捷图标,则不创建,直接返回
        if (PreferenceUtils.getBoolean(this, Config.KEY_SHORTCUT)) {
            return;
        }
        Intent intent = new Intent();//隐式意图
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");//设置动作
        //显示图标图像
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.ic_default));
        //显示图标名称
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机安全卫视快捷图标");
        //快捷图标点击行为
        Intent clickIntent = new Intent();
        clickIntent.setAction("com.example.mymobilesafe.home");
        clickIntent.addCategory("android.intent.category.LAUNCHER");
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, clickIntent);
        sendBroadcast(intent);
        PreferenceUtils.setBoolean(this, Config.KEY_SHORTCUT, true);
    }

    /**
     * 拷贝数字地址数据库
     */
    private void copyNumberAddressDB() {
        InputStream stream = null;
        FileOutputStream fos = null;
        try {
            File file = new File(getFilesDir(), "address.zip");
            if (file.exists()) {
                return;
            }
            AssetManager assets = this.getAssets();
            stream = assets.open("address.zip");
            fos = new FileOutputStream(file);
            int len = -1;
            byte[] bytes = new byte[1024];
            while ((len = stream.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtils.closeIO(fos);
            StreamUtils.closeIO(stream);
        }

    }

    /**
     * 拷贝日常型数据库
     */
    private void copyCommonNumberDB() {
        InputStream stream = null;
        FileOutputStream fos = null;
        try {
            File file = new File(getFilesDir(), "commonnum.db");
            if (file.exists()) {
                return;
            }
            AssetManager assets = this.getAssets();
            stream = assets.open("commonnum.db");
            fos = new FileOutputStream(file);
            int len;
            byte[] bytes = new byte[1024];
            while ((len = stream.read()) != -1) {
                fos.write(bytes, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtils.closeIO(fos);
            StreamUtils.closeIO(stream);
        }

    }

    /**
     * 拷贝危险型数据库
     */
    private void copyAntivirusDB() {
        Log.d(TAG, "copyAntivirusDB: ");
        InputStream stream = null;
        FileOutputStream fos = null;
        try {
            File file = new File(getFilesDir(), "commonnum.db");
            if (file.exists()) {
                return;
            }
            AssetManager assets = this.getAssets();
            stream = assets.open("commonnum.db");
            fos = new FileOutputStream(file);
            int len = -1;
            byte[] bytes = new byte[1024];
            while ((len = stream.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
            }
            fos.close();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtils.closeIO(fos);
            StreamUtils.closeIO(stream);
        }
    }

    /**
     * 进入到主页面
     */
    private void load2Home() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "curentThread: " + Thread.currentThread().getName());
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2 * 1000);
    }

    private void checkVersionUpdate() {
        new Thread(new CheckVersionTask()).start();
    }

    private class CheckVersionTask implements Runnable {
        @Override
        public void run() {
            String path = "http://192.168.0.100:8080/update.json";
            try {
                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5 * 1000);
                conn.setReadTimeout(2 * 1000);
                InputStream inputStream = conn.getInputStream();
                String json = StreamUtils.parseStream(inputStream);
                Log.d(TAG, "json: " + json);
                JSONObject jsonObject = new JSONObject(json);
                //网络最新版本号
                int netVersionCode = jsonObject.getInt("versionCode");
                String description = jsonObject.getString("description");
                mDownLoadUrl = jsonObject.getString("downloadUrl");
                String versionName = jsonObject.getString("versionName");

                Log.d(TAG, "versionCode: " + netVersionCode);
                Log.d(TAG, "description: " + description);
                Log.d(TAG, "downloadUrl: " + mDownLoadUrl);
                Log.d(TAG, "versionName: " + versionName);
                //本地版本号
                int localVersionCode = PackageUtils.getVersionCode(SplashActivity.this);

                //根据网络版本和本地版本进行对面,判断是否需要更新
                if (netVersionCode > localVersionCode) {
                    //提示用具进行更新
                    showUpdateDialog(description);
                } else {
                    //不更新, 结束当前页面,进入主页面
                    load2Home();

                }
            } catch (IOException e) {
                e.printStackTrace();
                notifyError("error:101");
            } catch (JSONException e) {
                e.printStackTrace();
                notifyError("error:102");
            }
        }
    }

    /**
     * 报错通知,携带内容为content
     *
     * @param content
     */
    private void notifyError(String content) {
        Message msg = new Message();
        msg.what = WHAT_SHOW_UPDATE_TOAST;
        msg.obj = content;
        mHandler.sendMessage(msg);
    }

    private void showUpdateDialog(String content) {
        //需要在主线程中更新
        Message msg = new Message();
        msg.what = WHAT_SHOW_UPDATE_DIALOG;
        msg.obj = content;
        mHandler.sendMessage(msg);
    }
}
