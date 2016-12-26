package sun.ch.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.logging.Filter;

import sun.ch.dao.AppLockDao;
import sun.ch.safe.LockActivity;

/**
 * Created by sunch on 2016/12/22.
 */
public class appLockService extends Service {

    private boolean flag = true;
    private String mSkipPackage;

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
        new Thread() {
            @Override
            public void run() {
                while (flag) {
                    AppLockDao dao = new AppLockDao(appLockService.this);
                    ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    List<ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(1);
                    ActivityManager.RunningTaskInfo task = runningTasks.get(0);
                    String packageName = task.topActivity.getPackageName();
                    boolean search = dao.search(packageName);
                    if (search && !packageName.equals(mSkipPackage)) {
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
}
