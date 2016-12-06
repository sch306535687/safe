package sun.ch.safe;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * 创建一个基类，实现滑动翻页代码
 */
public abstract class BaseActivity extends Activity {

    private GestureDetector mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //手势识别器手指滑动实现界面翻页
        mDetector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //限制y轴方向的滑动幅度
                if( Math.abs(e1.getRawY()-e2.getRawY()) > 200 ){
                    return true;
                }
                //限制滑动速度不能过快
                if( Math.abs(velocityX) < 100 ){
                    Toast.makeText(BaseActivity.this,"滑动太慢了",Toast.LENGTH_SHORT).show();
                    return true;
                }
                //向左滑，下一页
                if( (e1.getRawX()-e2.getRawX()) > 200){
                    nextPage();
                }
                //向右滑，上一页
                if( (e2.getRawX()-e1.getRawX()) > 200){
                    previousPage();
                }

                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);//委托事件到手势识别器处理
        return super.onTouchEvent(event);
    }

    /**
     * 跳到下一页
     */
    public abstract void nextPage();
    /**
     * 跳到上一页
     */
    public abstract void previousPage();

    /**
     * 点击下一页
     * @param view
     */
    public void next(View view){
        nextPage();
    }

    /**
     * 点击上一页
     * @param view
     */
    public void previous(View view){
        previousPage();
    }
}
