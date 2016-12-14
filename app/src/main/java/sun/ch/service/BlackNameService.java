package sun.ch.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

import sun.ch.dao.BlackNameDao;

/**
 * Created by asus on 2016/12/13.
 */
public class BlackNameService extends Service {
    private TelephonyManager manager;
    private BlackNameListener listener;
    private BlackNameDao dao;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        dao = new BlackNameDao(BlackNameService.this);//操作黑名单类
        manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new BlackNameListener();
        manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        // 动态配置拦截短信黑名单广播接收者
        BlackNameReceiver blackNameReceiver = new BlackNameReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(blackNameReceiver,filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //当服务关闭时，停止黑名单服务
        manager.listen(listener, PhoneStateListener.LISTEN_NONE);
    }

    /**
     * 监听电话黑名单
     */
    public class BlackNameListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, final String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING://正在响铃
                    //黑名单判断
                    String mode = dao.select(incomingNumber);
                    System.out.println("模式为"+mode);
                    if(!TextUtils.isEmpty(mode)){
                        if(mode.equals("1")||mode.equals("3")){
                            //清楚通话记录中号码
                            final Uri uri = Uri.parse("content://call_log/calls");
                            getContentResolver().registerContentObserver(uri, true, new ContentObserver(new Handler()) {
                                @Override
                                public void onChange(boolean selfChange) {
                                    getContentResolver().unregisterContentObserver(this);//反注册
                                    getContentResolver().delete(uri,"number=?",new String[]{incomingNumber});//调用删除方法
                                    super.onChange(selfChange);
                                }
                            });
                            //挂断电话
                            endCall();
                        }
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE://闲置状态

                    break;
            }
        }
    }

    /**
     * 拦截电话,注意配置权限
     */
    private void endCall() {
        try {
            Class<?> clazz = getClassLoader().loadClass("android.os.ServiceManager");
            Method method = clazz.getDeclaredMethod("getService", String.class);
            IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
            iTelephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 监听短信黑名单广播
     */
    public class BlackNameReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取intent参数
            Bundle bundle = intent.getExtras();
            //判断bundle内容
            if (bundle != null) {
                //取pdus内容,转换为Object[]
                Object[] pdus = (Object[]) bundle.get("pdus");
                for (Object pu : pdus) {
                    byte[] pu1 = (byte[]) pu;
                    SmsMessage msg = SmsMessage.createFromPdu(pu1);
                    //获取号码
                    String incomingNumber = msg.getOriginatingAddress();
                    //黑名单判断
                    String mode = dao.select(incomingNumber);
                    if (!TextUtils.isEmpty(mode)) {
                        if (mode.equals("1") || mode.equals("2")) {
                            abortBroadcast();
                        }
                    }
                }
            }
        }
    }
}
