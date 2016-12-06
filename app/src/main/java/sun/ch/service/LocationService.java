package sun.ch.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import java.util.List;

/**
 * Created by asus on 2016/12/5.
 */
public class LocationService extends Service {

    private MyLocationListener listener;
    private LocationManager manager;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        //判断是否为#*location*#,发送经纬度
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> allProviders = manager.getAllProviders();//获取所有的位置提供者
        Criteria criteria = new Criteria();//定义一个标准
        criteria.setCostAllowed(true);//允许收费
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//设置精确度
        //获取最好的位置提供者
        String bestProvider = manager.getBestProvider(criteria, true);//true代表可用时才返回
        //获取位置提供者
        listener = new MyLocationListener();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        manager.requestLocationUpdates(bestProvider, 0, 0, listener);

    }

    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            //保存经纬度
            sharedPreferences.edit().putString("location",latitude+":"+longitude).commit();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //关闭位置提供者
        manager.removeUpdates(listener);
    }
}
