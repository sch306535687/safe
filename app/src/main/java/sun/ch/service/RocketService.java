package sun.ch.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import sun.ch.safe.R;
import sun.ch.safe.RocketBackgroundActivity;

/**
 * Created by Administrator on 2016/12/9.
 */
public class RocketService extends Service {
    private static WindowManager manager;
    private static View view;
    private WindowManager.LayoutParams params;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //配置参数信息
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        //设置重心为左上角,默认为中心
        params.gravity = Gravity.LEFT + Gravity.TOP;
        view = View.inflate(this, R.layout.rocket,null);
        final ImageView rocket = (ImageView) view.findViewById(R.id.iv_rocket);

        //添加帧动画
        rocket.setBackgroundResource(R.drawable.rocket);
        AnimationDrawable rocketAnimation = (AnimationDrawable) rocket.getBackground();
        rocketAnimation.start();

        manager.addView(view, params);
        //获取屏幕宽高
        final int screenWidth = manager.getDefaultDisplay().getWidth();
        final int screenHeight = manager.getDefaultDisplay().getHeight();
        //拖拽事件
        view.setOnTouchListener(new View.OnTouchListener() {
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
                        //重新设置控件位置
                        params.x += x;
                        params.y += y;
                        //限制边界
                        if(params.x<0){
                            params.x = 0;
                        }
                        if(params.x>screenWidth-view.getWidth()){
                            params.x = screenWidth-view.getWidth();
                        }
                        if(params.y<0){
                            params.y = 0;
                        }
                        if(params.y>screenHeight-view.getHeight()){
                            params.y = screenHeight-view.getHeight();
                        }
                        //更新view
                        manager.updateViewLayout(view, params);
                        //重新获取初始坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP://手指抬起触发
                        System.out.println("x:"+ params.x+";y:"+ params.y);
                        if(params.x>120&& params.x<240&& params.y>screenHeight-150){
                            rocketShoot();//发射火箭
                            Intent intent = new Intent(RocketService.this, RocketBackgroundActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);//打开火箭背景
                        }
                        break;
                }
                return true;
            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int y = (int) msg.obj;
            params.y -= y;
            //更新view
            manager.updateViewLayout(view, params);
        }
    };
    /**
     * 发射火箭
     */
    private void rocketShoot() {
        new Thread(){
            @Override
            public void run() {
                for(int i=0;i<=10;i++){
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int y = i*40 ;
                    Message message = handler.obtainMessage(0, y);
                    handler.sendMessage(message);
                }
            }
        }.start();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
