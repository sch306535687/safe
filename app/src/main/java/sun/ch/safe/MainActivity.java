package sun.ch.safe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import sun.ch.service.RocketService;
import sun.ch.utils.md5;

/**
 * Created by sunch on 2016/11/29.
 */
public class MainActivity extends Activity {
    String[] strArr = new String[]{"手机防盗", "通讯卫士", "软件管理", "进程管理", "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"};
    int[] imageArr = new int[]{R.mipmap.home_safe, R.mipmap.home_2, R.mipmap.home_1,
            R.mipmap.home_taskmanager, R.mipmap.home_3, R.mipmap.home_trojan,
            R.mipmap.home_sysoptimize, R.mipmap.home_tools, R.mipmap.home_settings};
    private TextView password;
    private TextView confirmPassword;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置GridView
        GridView gvview = (GridView) findViewById(R.id.gv_view);
        gvview.setAdapter(new MyAdapt());
        //获取sharepreferences
        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);

        //监听gvview点击事件
        gvview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //手机防盗弹窗
                        showAlertDialog();
                        break;
                    case 1:
                        //通讯卫士
                        startActivity(new Intent(getApplicationContext(), BlackNameActivity.class));
                        break;
                    case 2:
                        //软件管理
                        startActivity(new Intent(getApplicationContext(), SoftWareManagerActivity.class));
                        break;
                    case 3:
                        //进程管理
                        startActivity(new Intent(getApplicationContext(), ProcessActivity.class));
                        break;
                    case 7:
                        //高级工具
                        startActivity(new Intent(getApplicationContext(), AdvanceToolsActivity.class));
                        break;
                    case 8:
                        //设置中心
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        break;
                }
            }
        });
        //开启小火箭
        startService(new Intent(this, RocketService.class));
    }

    protected void showAlertDialog() {
        //判断是否设置了密码
        String secret = sharedPreferences.getString("password", null);
        if (!TextUtils.isEmpty(secret)) {
            //如果设置了密码，则弹出输入密码框
            showSetSecretInputDialog();
        } else {
            //如果没有设置密码，则弹出设置密码框
            showSetSecretDialog();
        }
    }

    private void showSetSecretInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = View.inflate(MainActivity.this, R.layout.set_input_secret_dialog, null);
        builder.setView(view);
        final Dialog dialog = builder.create();
        dialog.show();
        //获取控件值
        final TextView inputPassword = (TextView) view.findViewById(R.id.tv_passpord);
        Button onBtn = (Button) view.findViewById(R.id.btn_on);
        Button offBtn = (Button) view.findViewById(R.id.btn_off);

        //监听确定按钮
        onBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取输入密码，判断
                String pass = inputPassword.getText().toString();
                //获取保存的密码
                String secret = sharedPreferences.getString("password", null);
                if (secret.equals(md5.MD5(pass))) {
                    //跳到手机防盗页面
                    startActivity(new Intent(MainActivity.this,StealActivity.class));
                    finish();
                } else {
                    //密码错误
                    Toast.makeText(MainActivity.this,"密码错误",Toast.LENGTH_SHORT).show();
                }


            }
        });
        //监听取消按钮
        offBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void showSetSecretDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = View.inflate(MainActivity.this, R.layout.set_secret_dialog, null);
        builder.setView(view);
        final Dialog dialog = builder.create();
        dialog.show();
        //获取控件值
        password = (TextView) view.findViewById(R.id.tv_passpord);
        confirmPassword = (TextView) view.findViewById(R.id.tv_passpord_confirm);
        Button onBtn = (Button) view.findViewById(R.id.btn_on);
        Button offBtn = (Button) view.findViewById(R.id.btn_off);

        //监听确定按钮
        onBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取控件值
                final String pass = password.getText().toString();
                final String confirmPass = confirmPassword.getText().toString();
                //判断控件值是否为空或null
                if (!TextUtils.isEmpty(pass) && !TextUtils.isEmpty(confirmPass)) {
                    //判断密码是否相等
                    if (pass.equals(confirmPass)) {
                        //密码正确，保存到sharepreferences
                        sharedPreferences.edit().putString("password",md5.MD5(pass)).commit();
                        dialog.dismiss();
                        //跳到手机防盗页面
                        startActivity(new Intent(MainActivity.this,StealActivity.class));
                        finish();
                        Toast.makeText(MainActivity.this,"密码设置成功",Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(MainActivity.this,"两次输入密码不相等",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this,"输入框不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //监听取消按钮
        offBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public class MyAdapt extends BaseAdapter {

        @Override
        public int getCount() {
            return strArr.length;
        }

        @Override
        public Object getItem(int position) {
            return strArr[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = View.inflate(getApplicationContext(), R.layout.items, null);
            ImageView ivimage = (ImageView) view.findViewById(R.id.iv_image);
            ivimage.setImageResource(imageArr[position]);
            TextView tvtext = (TextView) view.findViewById(R.id.tv_text);
            tvtext.setText(strArr[position]);
            return view;
        }
    }
}
