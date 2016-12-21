package sun.ch.safe;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import sun.ch.fragment.LockFragment;
import sun.ch.fragment.UnlockFragment;

/**
 * Created by sunch on 2016/12/21.
 */
public class AppLockActivity extends FragmentActivity {

    private TextView unlockBtn;
    private TextView lockBtn;
    private LockFragment lockFragment;
    private UnlockFragment unlockFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artivity_applock);
        unlockBtn = (TextView) findViewById(R.id.unlockBtn);
        lockBtn = (TextView) findViewById(R.id.lockBtn);
        init();
    }

    private void init() {
        lockFragment = new LockFragment();
        unlockFragment = new UnlockFragment();
        //获取
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();//注册事物
        //切换到未加锁界面
        transaction.replace(R.id.frame_contain, unlockFragment);
        transaction.commit();
        unlockBtn.setBackgroundResource(R.mipmap.tab_left_pressed);
        lockBtn.setBackgroundResource(R.mipmap.tab_left_default);
/**
         * 未加锁
         */
        unlockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();//注册事务
                //切换到未加锁界面
                transaction.replace(R.id.frame_contain, unlockFragment);
                transaction.commit();
                unlockBtn.setBackgroundResource(R.mipmap.tab_left_pressed);
                lockBtn.setBackgroundResource(R.mipmap.tab_left_default);
            }
        });
        /**
         * 加锁
         */
        lockBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();//注册事务
                //切换到加锁界面
                transaction.replace(R.id.frame_contain, lockFragment);
                transaction.commit();
                unlockBtn.setBackgroundResource(R.mipmap.tab_left_default);
                lockBtn.setBackgroundResource(R.mipmap.tab_left_pressed);
            }
        });
    }
}
