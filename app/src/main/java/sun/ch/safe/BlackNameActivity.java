package sun.ch.safe;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import sun.ch.bean.BlackNumberInfo;
import sun.ch.dao.BlackNameDao;

/**
 * Created by asus on 2016/12/9.
 */
public class BlackNameActivity extends Activity {

    private List<BlackNumberInfo> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blackname);
        //获取全部数据
        BlackNameDao dao = new BlackNameDao(this);
        list = dao.getAll();

        ListView listView = (ListView) findViewById(R.id.listview);
        MyAdapt adapt = new MyAdapt();
        listView.setAdapter(adapt);//初始化listview
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
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(BlackNameActivity.this, R.layout.blackname_items, null);
                Helper helper = new Helper();
                helper.number = (TextView) convertView.findViewById(R.id.tv_number);
                helper.mode = (TextView) convertView.findViewById(R.id.tv_mode);
                convertView.setTag(helper);
            } else {
                Helper helper = (Helper) convertView.getTag();
                TextView number = helper.getNumber();
                TextView mode = helper.getMode();
                BlackNumberInfo info = list.get(position);
                number.setText(info.getNumber());
                if (info.getMode().equals("1")) {
                    mode.setText("短信拦截+电话");
                } else if (info.getMode().equals("2")) {
                    mode.setText("短信拦截");
                } else if (info.getMode().equals("3")) {
                    mode.setText("电话拦截");
                }

            }
            return convertView;
        }
    }

    public class Helper {
        TextView number;
        TextView mode;

        public TextView getNumber() {
            return number;
        }

        public TextView getMode() {
            return mode;
        }
    }
}
