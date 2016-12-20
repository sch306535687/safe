package sun.ch.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sun.ch.bean.Info;

/**
 * 获取手机所有应用信息
 */
public class AppInfos {
    /**
     * 获取手机所有应用程序
     * @return
     */
    public  List<Info> getAppInfos(Context context){
        List<Info> list = new ArrayList<Info>();

        PackageManager manager = context.getPackageManager();//获取包管理器
        List<PackageInfo> packages = manager.getInstalledPackages(0);//获取所有应用的集合
        //遍历集合
        for(PackageInfo app:packages){
            Info info = new Info();
            String  appName = (String) app.applicationInfo.loadLabel(manager);//获取应用名
            Drawable appIcon = app.applicationInfo.loadIcon(manager);//获取应用图标
            String sourceDir = app.applicationInfo.sourceDir;//获取应用路径
            String packageName = app.packageName;//获取应用包名
            File file = new File(sourceDir);
            long appSize = file.length();//获取应用大小
            int flags = app.applicationInfo.flags;
            boolean isSystemApp;
            boolean isSDCard;
            //判断应用是系统应用还是用户应用
            if((flags & ApplicationInfo.FLAG_SYSTEM)!=0){
                //系统应用
                isSystemApp = true;
            }else{
                //用户应用
                isSystemApp = false;
            }
            //判断应用是保存在sd卡还是内存中
            if((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE)!=0){
                //sd卡
                isSDCard = true;
            }else{
                //内存
                isSDCard = false;
            }
            //封装info
            info.setAppName(appName);
            info.setAppSize(appSize);
            info.setAppIcon(appIcon);
            info.setPackAgeName(packageName);
            info.setSystemApp(isSystemApp);
            info.setSDCard(isSDCard);
            list.add(info);
        }

        return list;
    }
}
