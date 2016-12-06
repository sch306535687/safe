package sun.ch.safe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/12/1.
 */
public class Step3Activity extends BaseActivity {

    private TextView safePhone;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step3);
        safePhone = (TextView) findViewById(R.id.safe_phone);
        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        //一进入当前页面就给控件赋值
        String safe_phone = sharedPreferences.getString("safe_phone", "");
        safePhone.setText(safe_phone);

    }

    /**
     * 选择联系人
     */
    public void selectContact(View view) {
        //获取
        startActivityForResult(new Intent(Step3Activity.this, ContactActivity.class), 0);
    }

    /**
     * 获取联系人界面返回的数据
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //判断返回的结果码是否一致，避免直接点击手机返回键后崩溃（空指针异常）
        if (resultCode == Activity.RESULT_OK) {
            //获取返回的安全号码
            String phone = data.getStringExtra("phone");
            //设置号码到输入框
            safePhone.setText(phone);
            //把号码保存起来
            sharedPreferences.edit().putString("safe_phone", phone).commit();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void nextPage() {
        //判断输入框是否有值，有则跳到下一步，没有则提示输入电话信息
        String phone = safePhone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            //空的
            Toast.makeText(this,"请先输入安全号码",Toast.LENGTH_SHORT).show();
        } else {
            //跳转到第四步
            startActivity(new Intent(Step3Activity.this, Step4Activity.class));
            //设置切换动画，从右边进入，左边退出
            overridePendingTransition(R.anim.next_in, R.anim.next_out);
        }

    }

    @Override
    public void previousPage() {
        //跳转到第二步
        startActivity(new Intent(Step3Activity.this, Step2Activity.class));
        //设置切换动画，从左边边进入，右边边退出
        overridePendingTransition(R.anim.previous_in, R.anim.previous_out);
    }

}
