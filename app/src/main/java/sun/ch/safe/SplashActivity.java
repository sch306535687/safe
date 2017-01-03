package sun.ch.safe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import sun.ch.utils.StreamUtils;

public class SplashActivity extends Activity {

    private static final int UPDATE_MSG = 0;
    private static final int NET_ERROR = 1;
    private static final int URL_ERROR = 2;
    private static final int JSON_ERROR = 3;
    private static final int ALERT_MSG = 4;
    private static final int DELAY_MSG = 5;
    private String versionName;
    private int versionCode;
    private String description;
    private String downloadUrl;
    private TextView tvProgress;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_MSG:
                    alertShowDialog();
                    break;
                case NET_ERROR:
                    Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_SHORT).show();
                    alertMainActivity();
                    break;
                case URL_ERROR:
                    Toast.makeText(getApplicationContext(), "连接异常", Toast.LENGTH_SHORT).show();
                    alertMainActivity();
                    break;
                case JSON_ERROR:
                    Toast.makeText(getApplicationContext(), "获取数据异常", Toast.LENGTH_SHORT).show();
                    alertMainActivity();
                    break;
                case ALERT_MSG:
                    alertMainActivity();
                    break;
                case DELAY_MSG:
                    alertMainActivity();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //设置版本号
        TextView tvVersion = (TextView) findViewById(R.id.tv_version);
        tvProgress = (TextView) findViewById(R.id.tv_progress);
        tvVersion.setText("版本名:" + getVersionName());
        //判断是否需要提示更新版本信息
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        //获取SharedPreferences中信息
        boolean isChecked = sharedPreferences.getBoolean("isChecked", true);
        //判断是否需要提示更新
        if (isChecked) {
            //获取更新的json数据并判断是否有新版本
            getUpdateJson();
        } else {
            //发送延时消息，2s后再跳转到主页面
            handler.sendEmptyMessageDelayed(DELAY_MSG, 2000);
        }
        //设置渐变动画效果
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.splash);
        AlphaAnimation anim = new AlphaAnimation(0.3f, 1);
        anim.setDuration(2000);
        layout.startAnimation(anim);

        //初始化时，把手机号归属地数据库拷贝到/data/data/sun.ch.safe/files目录下
        StreamUtils.copyDatabase("address.db",this);
        //初始化时，把病毒数据库拷贝到/data/data/sun.ch.safe/files目录下
        StreamUtils.copyDatabase("antivirus.db",this);
        addShortCut();//往桌面添加快捷方式
    }

    /**
     * 往桌面添加快捷方式
     */
    public void addShortCut(){
        //往桌面添加快捷方式
        Intent intent = new Intent();
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,"安全卫士");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,R.mipmap.ic_launcher);
        intent.putExtra("duplicate",false);//不允许重复创建图标
        Intent doIntent = new Intent();//点击图标打开主页
        doIntent.setAction("main");//必须使用隐式方式打开
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,doIntent);
        sendBroadcast(intent);//发送添加快捷方式广播
    }

    /**
     * 获取版本名
     *
     * @return
     */
    public String getVersionName() {
        String versionName = "";
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            //获取版本名称
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 弹出对话框，判断是否有最新版本，是否下载或直接跳转
     */
    private void alertShowDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);//注意这里不能用getApplication(),因为没有token
        builder.setTitle("最新版本为" + versionName);
        builder.setMessage(description);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //执行下载方法
                download();
            }
        });
        builder.setNegativeButton("稍后更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //跳转主界面
                alertMainActivity();
            }
        });
        //设置取消侦听，点击返回键时出发
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //跳转主界面
                alertMainActivity();
            }
        });
        builder.show();

    }

    /**
     * 下载最新版本的apk
     */
    private void download() {
        //判断sdcard是否存在
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //显示进度
            tvProgress.setVisibility(View.VISIBLE);
            //使用xutils下载最新版本到sdcard
            HttpUtils http = new HttpUtils();
            http.download(downloadUrl,
                    Environment.getExternalStorageDirectory() + "/update.apk",
                    true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                    true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                    new RequestCallBack<File>() {
                        //正在下载
                        @Override
                        public void onLoading(long total, long current, boolean isUploading) {
                            tvProgress.setText("当前进度为" + current * 100 / total + "%");
                        }

                        @Override
                        public void onSuccess(ResponseInfo<File> responseInfo) {
                            //跳到系统安装页面安装应用
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.setDataAndType(Uri.fromFile(responseInfo.result), "application/vnd.android.package-archive");
                            startActivityForResult(intent, 0);
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {

                        }
                    });

        } else {
            Toast.makeText(getApplicationContext(), "找不到sd卡", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 侦听更新事件
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //跳转主界面
        alertMainActivity();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 跳到主界面
     */
    private void alertMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        //杀死当前activity
        finish();
    }

    /**
     * 获取版本号
     *
     * @return 版本号
     */
    public int getVersionCode() {
        int versionCode = 0;
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            //获取版本名称
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public void getUpdateJson() {
        //开启线程
        new Thread() {
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                HttpURLConnection conn = null;
                long currentTime = 0;
                try {
                    URL url = new URL("http://192.168.8.24:8888/update.json");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    if (conn.getResponseCode() == 200) {
                        InputStream inputStream = conn.getInputStream();
                        String resule = StreamUtils.readFromStream(inputStream);
                        //读取为json
                        JSONObject jsonObject = new JSONObject(resule);
                        //获取版本名
                        versionName = jsonObject.getString("versionName");
                        //获取版本号
                        versionCode = jsonObject.getInt("versionCode");
                        //获取描述
                        description = jsonObject.getString("description");
                        //获取新版本下载地址
                        downloadUrl = jsonObject.getString("downloadUrl");
                        //本地版本号
                        int currentCode = getVersionCode();
                        //判断本地版本号是否小于服务器的版本号
                        if (currentCode < versionCode) {
                            msg.what = UPDATE_MSG;
                            currentTime = System.currentTimeMillis();
                        } else {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            msg.what = ALERT_MSG;
                        }

                    }
                } catch (MalformedURLException e) {
                    msg.what = URL_ERROR;
                } catch (IOException e) {
                    msg.what = NET_ERROR;
                } catch (JSONException e) {
                    msg.what = JSON_ERROR;

                } finally {
                    long thisTime = System.currentTimeMillis();
                    long time = thisTime - currentTime;
                    if (time < 2000) {
                        try {
                            Thread.sleep(2000 - time);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    handler.sendMessage(msg);
                    //断开连接
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }.start();

    }


}
