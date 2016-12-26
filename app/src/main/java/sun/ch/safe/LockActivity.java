package sun.ch.safe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by sunch on 2016/12/22.
 */
public class LockActivity extends Activity {

    private EditText et_pwd;
    private Button btn_sure;
    private String aPackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock_activity);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        btn_sure = (Button) findViewById(R.id.btn_sure);
        final Intent intent = getIntent();
        //获取打开的应用包名
        aPackage = intent.getStringExtra("package");
    }

    /**
     * 点击确定
     * @param view
     */
    public void sure(View view){
        String pwd = et_pwd.getText().toString().trim();
        if (!TextUtils.isEmpty(pwd)) {
            if (pwd.equals("123")) {
                //密码输入正确,发送广播到appLockService,不要拦截该应用
                Intent intent1 = new Intent();
                intent1.setAction("sun.ch.safe.lock");
                intent1.putExtra("package",aPackage);//把该应用包名传递过去
                sendBroadcast(intent1);
                finish();
            } else {
                Toast.makeText(LockActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(LockActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 监听返回键,跳到桌面
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory("android.intent.category.HOME");
        startActivity(intent);
    }
}
