package sun.ch.safe;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

import sun.ch.dao.AntivirusDao;
import sun.ch.utils.md5;

/**
 * Created by sunch on 2016/12/20.
 */
public class AntivirusActivity extends Activity {
    private static final int CHECKING = 0;
    private static final int FINISH = 1;
    private static final int BEGIN = 2;
    private LinearLayout virus_list;
    private ProgressBar progressBar;
    private TextView antivirus_text;
    private List<PackageInfo> installedPackages;
    private ImageView scan_move;
    private RotateAnimation rotateAnimation;
    private ScrollView scroll_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acrivity_antivirus);
        virus_list = (LinearLayout) findViewById(R.id.virus_list);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        antivirus_text = (TextView) findViewById(R.id.antivirus_text);
        scan_move = (ImageView) findViewById(R.id.scan_move);
        scroll_view = (ScrollView) findViewById(R.id.scroll_view);

        //扫描动画
        rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1500);
        rotateAnimation.setInterpolator(new LinearInterpolator());//设置匀速旋转
        rotateAnimation.setRepeatCount(-1);
        scan_move.startAnimation(rotateAnimation);
        init();//初始化
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHECKING:
                    antivirus_text.setText("正在查杀病毒中");
                    AppInfos appInfo = (AppInfos) msg.obj;
                    TextView textView = new TextView(AntivirusActivity.this);
                    if (appInfo.isVirus) {
                        textView.setTextColor(Color.RED);
                        textView.setText(appInfo.appName+"--携带病毒");
                    } else {
                        textView.setTextColor(Color.BLACK);
                        textView.setText(appInfo.appName+"--安全");
                    }
                    virus_list.addView(textView,0);//把textView添加到线性布局的最前面
                    scroll_view.post(new Runnable() {
                        @Override
                        public void run() {
                            scroll_view.fullScroll(ScrollView.FOCUS_DOWN);//进度条自动向下滑动
                        }
                    });
                    break;
                case BEGIN:
                    progressBar.setMax(installedPackages.size());//初始化进度条最大值
                    break;
                case FINISH:
                    antivirus_text.setText("病毒查杀完毕");
                    scan_move.clearAnimation();
                    break;
            }
        }
    };

    private void init() {
        new Thread() {
            @Override
            public void run() {
                PackageManager packageManager = getPackageManager();
                //获取已安装app，某些app卸载后，还残留data/data目录数据也要加载出来PackageManager.GET_UNINSTALLED_PACKAGES
                installedPackages = packageManager.getInstalledPackages(0);
                Message message = handler.obtainMessage();
                message.what = BEGIN;
                handler.sendMessage(message);
                int progress = 0;
                for (PackageInfo info : installedPackages) {
                    AppInfos appInfo = new AppInfos();
                    String appName = (String) info.applicationInfo.loadLabel(packageManager);//应用名称
                    appInfo.appName = appName;

                    String sourceDir = info.applicationInfo.sourceDir;//应用源地址

                    String appMd = md5.getAppMd(sourceDir);//获取应用特征码

                    String desc = AntivirusDao.checkVirus(appMd);//从数据库中获取数据

                    if (desc == null) {
                        appInfo.isVirus = false;
                    } else {
                        appInfo.isVirus = true;
                    }
                    message = handler.obtainMessage();
                    message.what = CHECKING;
                    message.obj = appInfo;
                    handler.sendMessage(message);
                    //进度条
                    progress++;
                    progressBar.setProgress(progress);
                }
                message = handler.obtainMessage();
                message.what = FINISH;
                handler.sendMessage(message);

            }
        }.start();

    }

    private class AppInfos {
        private boolean isVirus;
        private String appName;
    }
}
