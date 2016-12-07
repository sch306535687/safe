package sun.ch.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import sun.ch.dao.AddressDao;
import sun.ch.receiver.OutgoingReceiver;
import sun.ch.utils.ShowWindowManager;

/**
 * Created by Administrator on 2016/12/7.
 */
public class PhoneAddressService extends Service {

    private TelephonyManager manager;
    private MyListener listener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyListener();
        manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        //当服务开启时，同时开启电话广播接收者
        // 动态配置拦截电话广播接收者
        OutgoingReceiver outgoingReceiver = new OutgoingReceiver();
        IntentFilter filter = new IntentFilter("android.intent.action.NEW_OUTGOING_CALL");
        registerReceiver(outgoingReceiver,filter);
    }

    public class MyListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING://正在响铃
                    String address = AddressDao.getAddress(incomingNumber);//获取来电归属地
                    //Toast.makeText(PhoneAddressService.this,address,Toast.LENGTH_LONG).show();
                    //弹出浮窗
                    ShowWindowManager.showWindow(PhoneAddressService.this,address);
                    break;
                case TelephonyManager.CALL_STATE_IDLE://关掉呼叫
                    //关闭浮窗
                    ShowWindowManager.closeWindow();
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //当服务关闭时，停止电话管理器
        manager.listen(listener, PhoneStateListener.LISTEN_NONE);
    }
}
