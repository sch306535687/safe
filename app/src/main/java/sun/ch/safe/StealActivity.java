package sun.ch.safe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/12/1.
 */
public class StealActivity extends Activity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        boolean configed = sharedPreferences.getBoolean("configed", false);
        //判断是否设置过引导页
        if (configed) {
            //设置过引导页
            setContentView(R.layout.activity_steal);
            //初始化值
            TextView safePhone = (TextView) findViewById(R.id.safe_phone);
            ImageView safeImage = (ImageView) findViewById(R.id.safe_image);
            String phoneString = sharedPreferences.getString("safe_phone", "");
            boolean protect = sharedPreferences.getBoolean("protect", false);
            safePhone.setText(phoneString);//设置安全号码的值
            if (protect) {
                //开启安全保护
                safeImage.setImageResource(R.mipmap.lock);
            } else {
                //没有开启安全保护
                safeImage.setImageResource(R.mipmap.unlock);
            }

        } else {
            //没有设置过引导页
            startActivity(new Intent(StealActivity.this, Step1Activity.class));
        }

    }

    public void next(View view) {
        startActivity(new Intent(StealActivity.this, Step1Activity.class));
        //设置切换动画，从右边进入，左边退出
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
    }
}
