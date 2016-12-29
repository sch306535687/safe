package sun.ch.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import sun.ch.bean.Info;
import sun.ch.dao.AppLockDao;
import sun.ch.safe.LockActivity;

/**
 * Created by sunch on 2016/12/22.
 */
public class appLockService extends Service {

    private boolean flag = true;
    private String mSkipPackage;
    private AppLockDao dao;
    private List<String> infos;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //注册广播
        MyReceiver myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("sun.ch.safe.lock");
        registerReceiver(myReceiver, filter);

        dao = new AppLockDao(appLockService.this);
        infos = dao.findAll();


        //注册内容观察者
        getContentResolver().registerContentObserver(Uri.parse("content://sun.ch.safe.change"),true,new MyObserver(new Handler()));
        new Thread() {
            @Override
            public void run() {

                while (flag) {
                    AppLockDao dao = new AppLockDao(appLockService.this);
                    ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    List<ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(1);
                    ActivityManager.RunningTaskInfo task = runningTasks.get(0);
                    String packageName = task.topActivity.getPackageName();
                    //boolean search = dao.search(packageName);
                        if (infos.contains(packageName) && !packageName.equals(mSkipPackage)) {
                            //加锁应用
                            Intent intent = new Intent(appLockService.this, LockActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("package",packageName);
                            startActivity(intent);
                        }
                    }



            }
        }.start();

    }

    @Override
    public void onDestroy() {
        flag = false;
        super.onDestroy();
        getContentResolver().registerContentObserver(Uri.parse("content://sun.ch.safe.change"),true,null);
    }
    /**
     * 注册广播
     *
     */
    private class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            mSkipPackage = intent.getStringExtra("package");
        }
    }

    //监听内容观察者
    class MyObserver extends ContentObserver{

        public MyObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            infos = dao.findAll();
            System.out.println("111");
        }
    }
}
