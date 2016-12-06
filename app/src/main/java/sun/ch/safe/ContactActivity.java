package sun.ch.safe;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/12/2.
 */
public class ContactActivity extends Activity {

    private ArrayList<HashMap<String, String>> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        data = getContactList();
        ListView listView = (ListView) findViewById(R.id.contact_list);
        listView.setAdapter(new SimpleAdapter(this, data, R.layout.contact_item, new String[]{"name", "phone"}, new int[]{R.id.name, R.id.phone}));
        //监听条目点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> map = data.get(position);
                //获取当前条目的电话号码
                String phone = map.get("phone").trim();
                //去除号码中的空格和空字符
                phone = phone.replaceAll("-","").replaceAll(" ","");
                //把数据放入意图
                Intent intent = new Intent();
                intent.putExtra("phone",phone);
                //通过意图把数据返回给Step3Activity
                setResult(Activity.RESULT_OK,intent);
                //杀死当前activity
                finish();
            }
        });

    }

    private ArrayList<HashMap<String, String>> getContactList() {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Cursor contact_id_cursor = getContentResolver().query(uri, new String[]{"contact_id"}, null, null, null);
        if (contact_id_cursor != null) {
            while (contact_id_cursor.moveToNext()) {
                HashMap<String, String> map = new HashMap<String, String>();
                String contact_id = contact_id_cursor.getString(0);
                //System.out.println(contact_id);
                Uri dataUri = Uri.parse("content://com.android.contacts/data");
                Cursor cursor = getContentResolver().query(dataUri, new String[]{"mimetype", "data1"}, "contact_id=?", new String[]{contact_id}, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String mimetype = cursor.getString(0);
                        String data1 = cursor.getString(1);
                        if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {
                            map.put("phone", data1);
                        } else if (mimetype.equals("vnd.android.cursor.item/name")) {
                            map.put("name", data1);
                        }
                    }
                    list.add(map);
                }
            }
            //关闭cursor类
            contact_id_cursor.close();
        }
        return list;
    }
}
