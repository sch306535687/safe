package sun.ch.safe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import sun.ch.view.Settings_item;

/**
 * Created by Administrator on 2016/12/1.
 */
public class Step2Activity extends BaseActivity {

    private Settings_item simSetting;
    private SharedPreferences sharedPreferences;
    private String simSerialNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step2);
        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        //获取当前手机sim卡序列号
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //获取sim卡序列号
        simSerialNumber = tm.getSimSerialNumber();
        //绑定sim卡
        simSetting = (Settings_item) findViewById(R.id.setting);
        //先判断先前是否保存是序列号
        String  sim = sharedPreferences.getString("sim", null);
        if (!TextUtils.isEmpty(sim)) {
            //先前设置过,初始状态设置为true
            simSetting.setCheck(true);
        } else {
            simSetting.setCheck(false);
        }
        simSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = simSetting.getCheck();
                if (checked) {
                    simSetting.setCheck(false);
                    //删除保存的sim卡序列号
                    sharedPreferences.edit().remove("sim").commit();
                } else {
                    simSetting.setCheck(true);
                    //保存sim卡序列号
                    sharedPreferences.edit().putString("sim", simSerialNumber).commit();

                }
            }
        });

    }

    @Override
    public void nextPage() {
        //判断是否绑定sim卡，绑定了才能跳到下一页
        String  sim = sharedPreferences.getString("sim", null);
        if (TextUtils.isEmpty(sim)) {
            //没有绑定sim卡
            Toast.makeText(this,"请先绑定sim卡",Toast.LENGTH_SHORT).show();
        } else {
            //已经绑定了sim卡
            //跳转到第三步
            startActivity(new Intent(Step2Activity.this, Step3Activity.class));
            //设置切换动画，从右边进入，左边退出
            overridePendingTransition(R.anim.next_in, R.anim.next_out);
        }
    }

    @Override
    public void previousPage() {
        //跳转到第一步
        startActivity(new Intent(Step2Activity.this, Step1Activity.class));
        //设置切换动画，从左边边进入，右边边退出
        overridePendingTransition(R.anim.previous_in, R.anim.previous_out);
    }

}
