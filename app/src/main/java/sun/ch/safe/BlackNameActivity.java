package sun.ch.safe;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import sun.ch.bean.BlackNumberInfo;
import sun.ch.dao.BlackNameDao;

/**
 * Created by asus on 2016/12/9.
 */
public class BlackNameActivity extends Activity {

    private List<BlackNumberInfo> list;
    private ListView listView;
    private BlackNameDao dao;
    private LinearLayout ll_progress;
    private int pageNumber = 0;//当前为第几页
    private int pageSize = 40;//一页显示几条数据
    private int pageCount;//总页数
    private MyAdapt adapt;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(adapt==null){
                adapt = new MyAdapt();
            }else{
                adapt.notifyDataSetChanged();
            }
            listView.setAdapter(adapt);//初始化listview
            ll_progress.setVisibility(View.INVISIBLE);//隐藏正在加载
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blackname);
        listView = (ListView) findViewById(R.id.listview);
        ll_progress = (LinearLayout) findViewById(R.id.ll_progress);
        dao = new BlackNameDao(this);
        pageCount = dao.getCount()%pageSize==0 ?dao.getCount()/pageSize:dao.getCount()/pageSize+1;
        initData();
    }

    /**
     * 初始化数据
     */
    public void initData() {
        ll_progress.setVisibility(View.VISIBLE);//显示正在加载
        new Thread() {
            @Override
            public void run() {
                //获取全部数据
                list = dao.getPageData(pageNumber, pageSize);
                if(list.size()>0){
                    handler.sendEmptyMessage(0);
                }
            }
        }.start();
    }

    /**
     * 上一页
     * @param view
     */
    public void prePage(View view) {
        if (pageNumber>=0) {
            pageNumber--;
            initData();
        }else{
            Toast.makeText(this,"当前已经为首页",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 下一页
     * @param view
     */
    public void nextPage(View view) {
        if (pageNumber<pageCount-1) {
            pageNumber++;
            initData();
        }else{
            Toast.makeText(this,"当前已经为最后一页",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 添加黑名单
     * @param view
     */
    public void addBlackName(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View addBlackView = View.inflate(this, R.layout.addblack_items, null);
        final EditText et_number = (EditText) addBlackView.findViewById(R.id.et_number);
        final CheckBox mode_phone = (CheckBox) addBlackView.findViewById(R.id.mode_phone);
        final CheckBox mode_msg = (CheckBox) addBlackView.findViewById(R.id.mode_msg);
        Button add_btn = (Button) addBlackView.findViewById(R.id.add_btn);
        Button dis_btn = (Button) addBlackView.findViewById(R.id.dis_btn);
        dialog.setView(addBlackView);
        dialog.show();

        //点击确定
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = et_number.getText().toString().trim();
                if(TextUtils.isEmpty(number)){
                    Toast.makeText(BlackNameActivity.this,"输入框不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                String mode = "";
                boolean mode_phoneChecked = mode_phone.isChecked();
                boolean mode_msgChecked = mode_msg.isChecked();
                if(mode_phoneChecked&&mode_msgChecked){
                    mode = "1";
                }else if(mode_msgChecked){
                    mode = "2";
                }else if(mode_phoneChecked){
                    mode = "3";
                }else {
                    Toast.makeText(BlackNameActivity.this,"请选择模式",Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean add = dao.add(number, mode);//添加数据
                if(add){
                    dialog.dismiss();
                    BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                    blackNumberInfo.setNumber(number);
                    blackNumberInfo.setMode(mode);
                    list.add(0,blackNumberInfo);
                    if(adapt==null){
                        adapt = new MyAdapt();
                    }else{
                        adapt.notifyDataSetChanged();
                    }
                    //initData();
                }
            }
        });
        //点击取消
        dis_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public class MyAdapt extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(BlackNameActivity.this, R.layout.blackname_items, null);
                Helper helper = new Helper();
                helper.number = (TextView) convertView.findViewById(R.id.tv_number);
                helper.mode = (TextView) convertView.findViewById(R.id.tv_mode);
                helper.clean = (ImageView) convertView.findViewById(R.id.clean);
                convertView.setTag(helper);
            } else {
                Helper helper = (Helper) convertView.getTag();
                final TextView number = helper.number;
                TextView mode = helper.mode;
                BlackNumberInfo info = list.get(position);
                number.setText(info.getNumber());
                if (info.getMode().equals("1")) {
                    mode.setText("短信拦截+电话");
                } else if (info.getMode().equals("2")) {
                    mode.setText("短信拦截");
                } else if (info.getMode().equals("3")) {
                    mode.setText("电话拦截");
                }
                //删除数据
                helper.clean.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dao.delete(number.getText().toString());//删除点击的数据
                        list.remove(list.get(position));
                        adapt.notifyDataSetChanged();//重新更新数据
                    }
                });

            }
            return convertView;
        }
    }

    public class Helper {
        TextView number;
        TextView mode;
        ImageView clean;
    }
}
