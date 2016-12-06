package sun.ch.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import sun.ch.safe.R;
import sun.ch.service.LocationService;


/**
 * Created by asus on 2016/12/5.
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {

    private SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
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
                System.out.println(content);
                if (content.equals("#*alarm*#")) {
                    //判断是否为#*alarm*#,播放音乐
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
                    mediaPlayer.setVolume(1f, 1f);//左右声道开到最大
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                } else if (content.equals("#*location*#")) {
                    //启动服务，获取经纬度
                    context.startService(new Intent(context, LocationService.class));
                    //获取经纬度
                    String location = sharedPreferences.getString("location", "location is getting");
                    //把经纬度通过短信发送到安全号码
                    SmsManager smsManager = SmsManager.getDefault();
                    String safe_phone = sharedPreferences.getString("safe_phone", "");
                    if(!TextUtils.isEmpty(safe_phone)){
                        //发送短信
                        smsManager.sendTextMessage(safe_phone,null,location,null,null);
                    }
                }
            }
        }
    }




}
