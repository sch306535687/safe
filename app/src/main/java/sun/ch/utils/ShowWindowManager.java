package sun.ch.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import sun.ch.safe.R;

/**
 * 创建可以在第三方应用中的浮窗
 */
public class ShowWindowManager {

    private static WindowManager manager;
    private static View view;

    /**
     * 弹出浮窗
     * @param context
     * @param msg
     */
    public static void showWindow(Context context,String msg){
        //自定义背景风格
        SharedPreferences sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //配置参数信息
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

        //设置重心为左上角,默认为中心
        params.gravity = Gravity.LEFT + Gravity.TOP;
        //设置浮窗位置
        int lastLeft = sharedPreferences.getInt("lastLeft", 0);
        int lastTop = sharedPreferences.getInt("lastTop", 0);
        params.x = lastLeft;
        params.y = lastTop;
        //引用view控件对象
        view = View.inflate(context, R.layout.activity_showwindow,null);
        TextView tvView = (TextView) view.findViewById(R.id.window_text);
        tvView.setText(msg);
        int[] styles = new int[]{R.mipmap.call_locate_white,R.mipmap.call_locate_orange,
                R.mipmap.call_locate_blue,R.mipmap.call_locate_gray,R.mipmap.call_locate_green};
        int window_style = sharedPreferences.getInt("window_style", 0);
        view.setBackgroundResource(styles[window_style]);
        //把view添加到window屏幕
        manager.addView(view,params);
    }
    public static void closeWindow(){
        if(manager!=null&&view!=null){
            manager.removeView(view);
            view = null;
        }
    }
}
