package sun.ch.safe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import sun.ch.utils.SmsUtils;

/**
 * Created by Administrator on 2016/12/7.
 */
public class AdvanceToolsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advancetools);
    }

    public void next(View view) {
        //跳到归属地查询界面
        startActivity(new Intent(this, QueryAddress.class));
    }

    /**
     * 短信备份
     *
     * @param view
     */
    public void saveMessage(View view) {
        //跳到归属地查询界面
        boolean b = SmsUtils.getSms(this);
        if (b) {
            Toast.makeText(this, "短信备份成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "短信备份失败", Toast.LENGTH_SHORT).show();
        }
    }
}
