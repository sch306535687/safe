package sun.ch.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.List;

import sun.ch.dao.AppLockDao;
import sun.ch.safe.LockActivity;

/**
 * Created by sunch on 2016/12/22.
 */
public class appLockService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        while (true){
            AppLockDao dao = new AppLockDao(this);
            ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(100);
            for(ActivityManager.RunningTaskInfo task:runningTasks){
                String packageName = task.topActivity.getPackageName();
                boolean search = dao.search(packageName);
                if(search){
                    //加锁应用
                    Intent intent = new Intent(this, LockActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else{
                    //未加锁应用
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
