package sun.ch.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by Administrator on 2016/12/7.
 */
public class ServiceRunning {
    public static boolean getServiceRunning(Context context,String serviceName){

        boolean flag = false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取所有正在运行的服务
        List<ActivityManager.RunningServiceInfo> services = manager.getRunningServices(100);
        //遍历集合
        for (ActivityManager.RunningServiceInfo service:services){
            String name = service.service.getClassName();
            //System.out.println(name);
            if(name.equals(serviceName)){
                flag = true;
            }
        }
        return flag;
    }
}
