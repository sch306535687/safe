package sun.ch.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class RootCompleteService extends BroadcastReceiver {

    private SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //获取sim卡序列号
        String simSerialNumber = tm.getSimSerialNumber();
        //获取sharepreferences中保存的sim卡序列号
        sharedPreferences = context.getSharedPreferences("config", context.MODE_PRIVATE);
        String sim = sharedPreferences.getString("sim", null);
        //判断防盗保护是否开启
        boolean protect = sharedPreferences.getBoolean("protect", false);
        if (protect) {
            //防盗保护已开启
            //获取预留的手机号
            String phone = sharedPreferences.getString("phone", null);
            if (sim.equals(simSerialNumber)) {
                //sim卡没有更换
                System.out.println("手机安全");
            } else {
                //发送手机不安全短信到预留的手机上
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone, null, "telephone has changed", null, null);
            }
        } else {
            //防盗保护没有开启

        }

    }
}
