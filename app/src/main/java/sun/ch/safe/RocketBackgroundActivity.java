package sun.ch.safe;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016/12/9.
 */
public class RocketBackgroundActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rocketbackground);
        ImageView bottom = (ImageView) findViewById(R.id.smoke_bottom);
        ImageView top = (ImageView) findViewById(R.id.smoke_top);

        //透明动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(1,0);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setFillAfter(true);
        bottom.startAnimation(alphaAnimation);
        top.startAnimation(alphaAnimation);
        //延迟1s后杀死当前activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },1000);
    }
}
