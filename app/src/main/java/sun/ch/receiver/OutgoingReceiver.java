package sun.ch.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import sun.ch.dao.AddressDao;
import sun.ch.utils.ShowWindowManager;

/**
 * Created by asus on 2016/12/7.
 */
public class OutgoingReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String resultData = getResultData();//获取外拨电话
        String address = AddressDao.getAddress(resultData);//获取归属地
        //Toast.makeText(context,address,Toast.LENGTH_LONG).show();
        //弹出浮窗
        ShowWindowManager.showWindow(context,address);
    }
}
