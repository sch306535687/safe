package sun.ch.safe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import sun.ch.service.BlackNameService;
import sun.ch.service.PhoneAddressService;
import sun.ch.service.appLockService;
import sun.ch.utils.ServiceRunning;
import sun.ch.view.Settings_click;
import sun.ch.view.Settings_item;

/**
 * Created by Administrator on 2016/11/30.
 */
public class SettingsActivity extends Activity {

    private SharedPreferences sharedPreferences;
    private Settings_click selectSstyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        setUpdate();//更新设置
        setAddress();//电话归属地设置
        setWindowStyle();//设置浮窗风格
        setShowWindowlocation();//设置归属地浮窗位置
        appLock();//程序锁
        //初始化保存的颜色风格
        int window_style = sharedPreferences.getInt("window_style", 0);
        String[] styles = new String[]{"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
        selectSstyle.setDesc(styles[window_style]);
        initBlackName();//拦截黑名单
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

    /**
     * 设置浮窗风格
     */
    public void setWindowStyle(){
        selectSstyle = (Settings_click) findViewById(R.id.showWindowStyle);
        //点击弹出风格单选框
        selectSstyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    public void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);//注意this的值
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("归属地提示框风格");
        final String[] styles = new String[]{"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
        //初始化保存的颜色风格
        int window_style = sharedPreferences.getInt("window_style", 0);
        builder.setSingleChoiceItems(styles, window_style, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //存储颜色索引值
                sharedPreferences.edit().putInt("window_style",which).commit();
                selectSstyle.setDesc(styles[which]);
                dialog.dismiss();//点击后直接退出
            }
        });
        builder.setNegativeButton("取消",null);//null表示点击取消退出单选弹窗
        builder.show();
    }

    /**
     * 设置归属地浮窗位置
     */
    public void setShowWindowlocation(){
        final Settings_click windowLocation = (Settings_click) findViewById(R.id.showWindowlocation);
        //初始化
        windowLocation.setTitle("归属地提示框位置");
        windowLocation.setDesc("设置归属地提示框的显示位置");
        windowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this,WindowLocation.class));
            }
        });
    }
    /**
     * 黑名单拦截
     */
    public void initBlackName() {
        final Settings_item blackName = (Settings_item) findViewById(R.id.blackName);
        //获取服务是否正在手机后台运行
        boolean serviceRunning = ServiceRunning.getServiceRunning(this, "sun.ch.service.BlackNameService");
        if (serviceRunning) {
            blackName.setCheck(true);//服务正在运行
        } else {
            blackName.setCheck(false);//服务停止运行
        }
        //监听自动更新设置
        blackName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = blackName.getCheck();
                //判断checkBox是否点击
                if (check) {
                    blackName.setCheck(false);
                    //关闭来电归属地服务
                    stopService(new Intent(SettingsActivity.this, BlackNameService.class));
                } else {
                    blackName.setCheck(true);
                    //开启来电归属地服务
                    startService(new Intent(SettingsActivity.this, BlackNameService.class));
                }
            }
        });
    }
    public void appLock() {
        final Settings_item applock = (Settings_item) findViewById(R.id.applock);
        //获取服务是否正在手机后台运行
        boolean serviceRunning = ServiceRunning.getServiceRunning(this, "sun.ch.service.appLockService");
        if (serviceRunning) {
            applock.setCheck(true);//服务正在运行
        } else {
            applock.setCheck(false);//服务停止运行
        }
        //监听自动更新设置
        applock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = applock.getCheck();
                //判断checkBox是否点击
                if (check) {
                    applock.setCheck(false);
                    //关闭来电归属地服务
                    stopService(new Intent(SettingsActivity.this, appLockService.class));
                } else {
                    applock.setCheck(true);
                    //开启来电归属地服务
                    startService(new Intent(SettingsActivity.this, appLockService.class));
                }
            }
        });
    }
}
