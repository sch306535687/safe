package sun.ch.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import sun.ch.dao.AddressDao;

/**
 * Created by Administrator on 2016/12/7.
 */
public class PhoneAddressService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        TelephonyManager  manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        MyListener listener = new MyListener();
        manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public class MyListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING://正在响铃
                    String address = AddressDao.getAddress(incomingNumber);//获取来电归属地
                    Toast.makeText(PhoneAddressService.this,address,Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
}
