package sun.ch.safe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
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
        //弹出进度条对话框
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("短信备份");
        progressDialog.setMessage("正在进行短信备份");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        new Thread(){
            @Override
            public void run() {
                //跳到归属地查询界面
                boolean b = SmsUtils.getSms(AdvanceToolsActivity.this, new SmsUtils.Progress() {//设置进度条进度
                    @Override
                    public void setCount(int count) {
                        progressDialog.setMax(count);
                    }

                    @Override
                    public void setProgress(int process) {
                        progressDialog.setProgress(process);
                    }
                });
                if (b) {
                    Looper.prepare();
                    progressDialog.dismiss();
                    Toast.makeText(AdvanceToolsActivity.this, "短信备份成功", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                } else {
                    Looper.prepare();
                    progressDialog.dismiss();
                    Toast.makeText(AdvanceToolsActivity.this, "短信备份失败", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }.start();

    }
}
