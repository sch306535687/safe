package sun.ch.safe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/12/1.
 */
public class Step4Activity extends BaseActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step4);
        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        CheckBox protect_check = (CheckBox) findViewById(R.id.checked);
        final TextView protect_desc = (TextView) findViewById(R.id.tv_desc);
        //初始化
        boolean protect = sharedPreferences.getBoolean("protect", false);
        if(protect){
            protect_check.setChecked(true);
            protect_desc.setText("防盗保护已开启");
        }else{
            protect_check.setChecked(false);
            protect_desc.setText("防盗保护没有开启");
        }
        //监听多选框的改变事件
        protect_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ( isChecked ) {
                    protect_desc.setText("防盗保护已开启");
                    sharedPreferences.edit().putBoolean("protect",true).commit();
                } else {
                    sharedPreferences.edit().putBoolean("protect",false).commit();
                    protect_desc.setText("防盗保护没有开启");
                }
            }
        });
    }

    @Override
    public void nextPage() {
        //存储是否完成引导页设置
        sharedPreferences.edit().putBoolean("configed", true).commit();
        //跳转到防盗页面
        startActivity(new Intent(Step4Activity.this, StealActivity.class));
        //设置切换动画，从右边进入，左边退出
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
    }

    @Override
    public void previousPage() {
        //跳转到第二步
        startActivity(new Intent(Step4Activity.this, Step3Activity.class));
        //设置切换动画，从左边边进入，右边边退出
        overridePendingTransition(R.anim.previous_in, R.anim.previous_out);
    }

}
