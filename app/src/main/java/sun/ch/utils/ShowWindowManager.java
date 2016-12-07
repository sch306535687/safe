package sun.ch.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * 创建可以在第三方应用中的浮窗
 */
public class ShowWindowManager {

    private static WindowManager manager;
    private static TextView view;

    /**
     * 弹出浮窗
     * @param context
     * @param msg
     */
    public static void showWindow(Context context,String msg){
        manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //配置参数信息
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.setTitle("toast");
        //创建TextView控件对象
        view = new TextView(context);
        view.setText(msg);
        //把view添加到window屏幕
        manager.addView(view,params);
    }
    public static void closeWindow(){
        if(manager!=null&&view!=null){
            manager.removeView(view);
        }
    }
}
