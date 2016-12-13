package sun.ch.receiver;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.widget.Toast;

import sun.ch.safe.R;
import sun.ch.service.LocationService;


/**
 * Created by asus on 2016/12/5.
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {

    private SharedPreferences sharedPreferences;
    private DevicePolicyManager mDPM;
    private ComponentName componentName;
    private boolean adminActive;

    @Override
    public void onReceive(final Context context, Intent intent) {
        sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        //获取intent参数
        Bundle bundle = intent.getExtras();
        //判断bundle内容
        if (bundle != null) {
            //取pdus内容,转换为Object[]
            Object[] pdus = (Object[]) bundle.get("pdus");

            for (Object pu : pdus) {
                byte[] pu1 = (byte[]) pu;
                SmsMessage msg = SmsMessage.createFromPdu(pu1);
                //获取短信内容
                String content = msg.getMessageBody();
                if (content.equals("#*alarm*#")) {
                    //判断是否为#*alarm*#,播放音乐
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
                    mediaPlayer.setVolume(1f, 1f);//左右声道开到最大
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                    //拦截短信
                    abortBroadcast();
                } else if (content.equals("#*location*#")) {
                    //启动服务，获取经纬度
                    context.startService(new Intent(context, LocationService.class));
                    //获取经纬度
                    String location = sharedPreferences.getString("location", "location is getting");
                    //把经纬度通过短信发送到安全号码
                    SmsManager smsManager = SmsManager.getDefault();
                    String safe_phone = sharedPreferences.getString("safe_phone", "");
                    if (!TextUtils.isEmpty(safe_phone)) {
                        //发送短信
                        smsManager.sendTextMessage(safe_phone, null, location, null, null);
                    }
                    //拦截短信
                    abortBroadcast();
                } else if (content.equals("#*lockscreen*#")) {
                    //锁屏代码
                    //获取设备管理器对象
                    mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                    //判断设备管理器是否已经激活
                    componentName = new ComponentName(context, AdminReceiver.class);
                    //判断是否激活
                    adminActive = mDPM.isAdminActive(componentName);
                    if (adminActive) {
                        mDPM.lockNow();//立即锁屏
                        mDPM.resetPassword("123456", 0);//设置开屏密码
                    } else {
                        //Toast.makeText(context,"请先激活设备管理器",Toast.LENGTH_SHORT).show();
                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("点击激活设备管理器");
                        builder.setPositiveButton("激活", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //代码激活设备管理器
                                Intent policyIntent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                                policyIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
                                policyIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Additional text explaining why this needs to be added.");
                                context.startActivity(policyIntent);
                            }
                        });
                        builder.setNegativeButton("取消激活", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AlertDialog alertDialog = builder.create();
                                alertDialog.dismiss();
                            }
                        });
                        builder.show();

                    }
                    //拦截短信
                    abortBroadcast();
                } else if (content.equals("#*wipedata*#")) {
                    //删除数据代码
                    adminActive = mDPM.isAdminActive(componentName);
                    if (adminActive) {
                        mDPM.wipeData(0);//恢复出厂设置，删除数据，除了外部设备
                    } else {
                        Toast.makeText(context, "请先激活设备管理器", Toast.LENGTH_SHORT).show();
                    }
                    //拦截短信
                    abortBroadcast();
                }
            }
        }
    }

}
