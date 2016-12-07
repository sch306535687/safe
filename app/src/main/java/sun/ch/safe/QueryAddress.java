package sun.ch.safe;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import sun.ch.dao.AddressDao;

/**
 * Created by Administrator on 2016/12/7.
 */
public class QueryAddress extends Activity {

    private EditText tvNumber;
    private TextView tvAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queryaddress);
        tvNumber = (EditText) findViewById(R.id.tv_number);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        //监听输入控件值的变化
        tvNumber.addTextChangedListener(new TextWatcher() {
            /**
             * 当值改变时调用
             * @param s
             * @param start
             * @param before
             * @param count
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String number = s.toString();//获取查询的号码
                String address = AddressDao.getAddress(number);//获取归属地地址
                tvAddress.setText(address);//设置归属地到控件显示
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void queryAddress(View view) {
        //查询归属地
        String number = tvNumber.getText().toString();//获取查询的号码
        String address = AddressDao.getAddress(number);//获取归属地地址
        tvAddress.setText(address);//设置归属地到控件显示
    }
}
