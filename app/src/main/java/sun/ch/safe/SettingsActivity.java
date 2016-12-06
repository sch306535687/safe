package sun.ch.safe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import sun.ch.view.Settings_item;

/**
 * Created by Administrator on 2016/11/30.
 */
public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Settings_item settingItem = (Settings_item) findViewById(R.id.setting);
        final SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        boolean isChecked = sharedPreferences.getBoolean("isChecked", true);
        //设置初始状态
        if(isChecked){
            settingItem.setCheck(true);
            //settingItem.setDesc("自动更新已经开启");
        }else{
            settingItem.setCheck(false);
            //settingItem.setDesc("自动更新已经关闭");
        }
        //监听自动更新设置
        settingItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = settingItem.getCheck();

                //判断checkBox是否点击
                if (check) {
                    settingItem.setCheck(false);
                    //settingItem.setDesc("自动更新已经关闭");
                    //把值存储到sharePreferrences
                    sharedPreferences.edit().putBoolean("isChecked",false).commit();
                } else {
                    settingItem.setCheck(true);
                    //settingItem.setDesc("自动更新已经开启");
                    //把值存储到sharePreferrences
                    sharedPreferences.edit().putBoolean("isChecked",true).commit();
                }
            }
        });
    }
}
