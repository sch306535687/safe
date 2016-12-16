package sun.ch.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import sun.ch.bean.ProcessInfo;
import sun.ch.safe.R;

/**
 * Created by asus on 2016/12/15.
 */
public class ProcessUtils {
    /**
     * 获取正在运行进程总数
     *
     * @param context
     * @return
     */
    public static int getProcessCount(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = manager.getRunningAppProcesses();
        return runningAppProcesses.size();
    }

    /**
     * 获取剩余内存
     *
     * @param context
     * @return
     */
    public static long getFreeStorage(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(memoryInfo);
        long availMem = memoryInfo.availMem;
        return availMem;
    }

    /**
     * 获取总内存
     *
     * @return
     */
    public static long getTotalStorage() {
        try {
            FileInputStream fis = new FileInputStream(new File("/proc/meminfo"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String s = reader.readLine();
            StringBuffer sb = new StringBuffer();
            for (char c : s.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    sb.append(c);
                }
            }
            return Long.parseLong(sb.toString()) * 1024;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取所有正在运行的进程的信息
     * @param context
     * @return
     */
    public static List<ProcessInfo> getProgressInfo(Context context) {
        List<ProcessInfo> processInfos = new ArrayList<ProcessInfo>();

        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses) {
            ProcessInfo processInfo = new ProcessInfo();
            try {
                String processName = info.processName;//获取进程名称
                processInfo.setProcessName(processName);
                //通过进程名称使用包管理器获取进程信息
                PackageManager packageManager = context.getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(processName, 0);
                Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);//获取进程图标
                processInfo.setIcon(icon);

                int pid = info.pid;
                Debug.MemoryInfo[] processMemoryInfo = manager.getProcessMemoryInfo(new int[]{pid});
                int totalPrivateDirty = processMemoryInfo[0].getTotalPrivateDirty();//获取进程占用的内存大小
                processInfo.setProcessSize(totalPrivateDirty);

                //判断进程是否为系统进程
                int flags = packageInfo.applicationInfo.flags;
                if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    //表示系统进程
                    processInfo.setSystem(true);
                    processInfo.setChecked(false);//默认选择
                } else {
                    //表示用户进程
                    processInfo.setSystem(false);
                    processInfo.setChecked(true);//默认选择
                }

                processInfos.add(processInfo);
            } catch (PackageManager.NameNotFoundException e) {
                processInfo.setIcon(context.getResources().getDrawable(R.mipmap.ic_launcher));
                processInfo.setProcessName("xxxx");
                e.printStackTrace();
            }
        }
        return processInfos;
    }

}
