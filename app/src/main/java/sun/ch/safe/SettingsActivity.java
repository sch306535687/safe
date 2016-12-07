package sun.ch.safe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import sun.ch.service.PhoneAddressService;
import sun.ch.utils.ServiceRunning;
import sun.ch.view.Settings_item;

/**
 * Created by Administrator on 2016/11/30.
 */
public class SettingsActivity extends Activity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        setUpdate();//更新设置
        setAddress();//电话归属地设置
    }

    /**
     * 设置自动更新
     */
    public void setUpdate() {
        final Settings_item settingItem = (Settings_item) findViewById(R.id.setting);
        boolean isChecked = sharedPreferences.getBoolean("isChecked", true);
        //设置初始状态
        if (isChecked) {
            settingItem.setCheck(true);
            //settingItem.setDesc("自动更新已经开启");
        } else {
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
                    sharedPreferences.edit().putBoolean("isChecked", false).commit();
                } else {
                    settingItem.setCheck(true);
                    //settingItem.setDesc("自动更新已经开启");
                    //把值存储到sharePreferrences
                    sharedPreferences.edit().putBoolean("isChecked", true).commit();
                }
            }
        });
    }

    /**
     * 设置电话归属地
     */
    public void setAddress() {
        final Settings_item setAddress = (Settings_item) findViewById(R.id.phoneAddress);
        //获取服务是否正在手机后台运行
        boolean serviceRunning = ServiceRunning.getServiceRunning(this, "sun.ch.service.PhoneAddressService");
        if (serviceRunning) {
            setAddress.setCheck(true);//服务正在运行
        } else {
            setAddress.setCheck(false);//服务停止运行
        }
        setAddress.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                boolean check = setAddress.getCheck();
                if (check) {
                    setAddress.setCheck(false);
                    //关闭来电归属地服务
                    stopService(new Intent(SettingsActivity.this, PhoneAddressService.class));
                } else {
                    setAddress.setCheck(true);
                    //开启来电归属地服务
                    startService(new Intent(SettingsActivity.this, PhoneAddressService.class));
                }
            }
        });
    }

}
