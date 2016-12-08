package sun.ch.safe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/12/8.
 */
public class WindowLocation extends Activity {

    private ImageView drag;
    private SharedPreferences sharedPreferences;
    private TextView top;
    private TextView bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.windowlocation);
        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        drag = (ImageView) findViewById(R.id.drag);
        top = (TextView) findViewById(R.id.text_top);
        bottom = (TextView) findViewById(R.id.text_bottom);
        //获取屏幕宽高
        final int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        final int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        //获取控件宽高
        final int width = drag.getWidth();
        final int height = drag.getHeight();
        //初始化位置
        int lastLeft = sharedPreferences.getInt("lastLeft", 0);
        int lastTop = sharedPreferences.getInt("lastTop", 0);
        //判断提示文字模块显示隐藏
        if (lastTop>screenHeight/2) {
            top.setVisibility(View.VISIBLE);
            bottom.setVisibility(View.INVISIBLE);
        } else {
            top.setVisibility(View.INVISIBLE);
            bottom.setVisibility(View.VISIBLE);
        }
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) drag.getLayoutParams();
        params.leftMargin = lastLeft;
        params.topMargin = lastTop;
        drag.setLayoutParams(params);

        //监听拖拽事件
        drag.setOnTouchListener(new View.OnTouchListener() {

            private int startY;
            private int startX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN://手指按下触发
                        //获取初始坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE://手指移动触发
                        //获取移动时的坐标
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();
                        //计算位移
                        int x = endX - startX;
                        int y = endY - startY;
                        //获取控件移动后的各个边距
                        int l = drag.getLeft() + x;
                        int t = drag.getTop() + y;
                        int r = drag.getRight() + x;
                        int b = drag.getBottom() + y;
                        //限制边界
                        if (l < 0 || r > screenWidth || t < 0 || b > screenHeight - 20) {
                            break;
                        }
                        //判断提示文字模块显示隐藏
                        if (t>screenHeight/2) {
                            top.setVisibility(View.VISIBLE);
                            bottom.setVisibility(View.INVISIBLE);
                        } else {
                            top.setVisibility(View.INVISIBLE);
                            bottom.setVisibility(View.VISIBLE);
                        }

                        drag.layout(l, t, r, b);//重新设置边距
                        //重新获取初始坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP://手指抬起触发
                        //存储坐标位置
                        int lastLeft = drag.getLeft();
                        int lastTop = drag.getTop();
                        sharedPreferences.edit().putInt("lastLeft", lastLeft).commit();
                        sharedPreferences.edit().putInt("lastTop", lastTop).commit();
                        break;
                }
                return true;
            }
        });

    }
}
