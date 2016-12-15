package sun.ch.safe;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import sun.ch.bean.ProcessInfo;
import sun.ch.utils.ProcessUtils;

/**
 * Created by asus on 2016/12/15.
 */
public class ProcessActivity extends Activity {

    @ViewInject(R.id.tv_process_count)
    private TextView tv_process_count;
    @ViewInject(R.id.tv_ram)
    private TextView tv_ram;
    @ViewInject(R.id.listview)
    private ListView listview;
    private List<ProcessInfo> progressInfos;
    private List<ProcessInfo> systemList;
    private List<ProcessInfo> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        ViewUtils.inject(this);
        init();
    }

    /**
     * 初始化数据
     */
    private void init() {
        int processCount = ProcessUtils.getProcessCount(this);
        long freeStorage = ProcessUtils.getFreeStorage(this);
        long totalStorage = ProcessUtils.getTotalStorage();
        tv_process_count.setText("运行中进程:" + processCount + "个");
        tv_ram.setText("剩余/总内存:" + Formatter.formatFileSize(this, freeStorage) + "/" +
                Formatter.formatFileSize(this, totalStorage));
        //初始化listview
        new Thread() {
            @Override
            public void run() {
                progressInfos = ProcessUtils.getProgressInfo(ProcessActivity.this);
                systemList = new ArrayList<ProcessInfo>();
                userList = new ArrayList<ProcessInfo>();
                for (ProcessInfo info : progressInfos) {
                    boolean systemProcess = info.isSystem();
                    if (systemProcess) {
                        systemList.add(info);
                    } else {
                        userList.add(info);
                    }
                }
                //直接在子线程中刷新ui
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProcessAdapter adapter = new ProcessAdapter();
                        listview.setAdapter(adapter);
                    }
                });
            }
        }.start();
    }

    private class ProcessAdapter extends BaseAdapter {

        private ViewHolder viewHolder;
        private ProcessInfo processInfo;
        @Override
        public int getCount() {
            return progressInfos.size()+2;
        }

        @Override
        public Object getItem(int position) {
            return progressInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, android.view.View convertView, ViewGroup parent) {
            if (position == 0) {
                TextView textView = new TextView(ProcessActivity.this);
                textView.setText("用户进程("+userList.size()+")");
                textView.setTextSize(18);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            } else if (position == (userList.size() + 1)) {
                TextView textView = new TextView(ProcessActivity.this);
                textView.setText("系统进程("+systemList.size()+")");
                textView.setTextSize(18);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            }
            if (position < (userList.size() + 1)) {
                processInfo = userList.get(position - 1);
            } else {
                processInfo = systemList.get(position - (userList.size() + 2));
            }

            if (convertView != null && convertView instanceof LinearLayout) {
                viewHolder = (ViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(ProcessActivity.this,R.layout.process_items,null);
                viewHolder = new ViewHolder();
                viewHolder.process_icon = (ImageView) convertView.findViewById(R.id.process_icon);
                viewHolder.process_name = (TextView) convertView.findViewById(R.id.process_name);
                viewHolder.process_size = (TextView) convertView.findViewById(R.id.process_size);
                viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
                convertView.setTag(viewHolder);
            }

            processInfo = progressInfos.get(position);
            viewHolder.process_icon.setBackgroundDrawable(processInfo.getIcon());
            viewHolder.process_name.setText(processInfo.getProcessName());
            viewHolder.process_size.setText("内存占用"+processInfo.getProcessSize());
            viewHolder.checkbox.setChecked(processInfo.isSystem());

            return convertView;
        }
    }

    public class ViewHolder {
        ImageView process_icon;
        TextView process_name;
        TextView process_size;
        CheckBox checkbox;
    }
}
