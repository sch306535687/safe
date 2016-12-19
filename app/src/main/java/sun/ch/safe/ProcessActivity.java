package sun.ch.safe;

import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    private ProcessAdapter adapter;
    private int processCount;
    private long freeStorage;
    private long totalStorage;

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
        processCount = ProcessUtils.getProcessCount(this);
        freeStorage = ProcessUtils.getFreeStorage(this);
        totalStorage = ProcessUtils.getTotalStorage();

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
                        tv_process_count.setText("运行中进程:" + (userList.size()+systemList.size()) + "个");
                        tv_ram.setText("剩余/总内存:" + Formatter.formatFileSize(ProcessActivity.this, freeStorage) + "/" +
                                Formatter.formatFileSize(ProcessActivity.this, totalStorage));
                        adapter = new ProcessAdapter();
                        listview.setAdapter(adapter);
                        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Object obj = listview.getItemAtPosition(position);
                                ViewHolder holder = (ViewHolder) view.getTag();
                                if (obj!=null && obj instanceof ProcessInfo) {
                                    ProcessInfo info = (ProcessInfo)obj;
                                    if(info.isChecked()){
                                        info.setChecked(false);
                                        holder.checkbox.setChecked(false);
                                    }else{
                                        info.setChecked(true);
                                        holder.checkbox.setChecked(true);
                                    };
                                }
                            }
                        });
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
            return userList.size()+systemList.size()+2;
        }

        @Override
        public Object getItem(int position) {
            if(position==0 || position==(userList.size() + 1)){
                return null;
            }
            if (position < (userList.size() + 1)) {
                processInfo = userList.get(position - 1);
            } else {
                processInfo = systemList.get(position - (userList.size() + 2));
            }
            return processInfo;
        }

        @Override
        public long getItemId(int position) {
            return position+2;
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

            viewHolder.process_icon.setBackgroundDrawable(processInfo.getIcon());
            viewHolder.process_name.setText(processInfo.getProcessName());
            viewHolder.process_size.setText("内存占用"+Formatter.formatFileSize(ProcessActivity.this,processInfo.getProcessSize()*1024));
            viewHolder.checkbox.setChecked(processInfo.isChecked());

            return convertView;
        }
    }

    public class ViewHolder {
        ImageView process_icon;
        TextView process_name;
        TextView process_size;
        CheckBox checkbox;
    }

    /**
     * 全选
     * @param view
     */
    public void selectAll(View view){
        for(ProcessInfo info:userList){
            if(!info.isChecked()){
                info.setChecked(true);
            }
        }
        for(ProcessInfo info:systemList){
            if(!info.isChecked()){
                info.setChecked(true);
            }
        }
        adapter.notifyDataSetChanged();//更新界面
    }
    /**
     * 反选
     * @param view
     */
    public void selectOppsite(View view){
        for(ProcessInfo info:userList){
           info.setChecked(!info.isChecked());
        }
        for(ProcessInfo info:systemList){
            info.setChecked(!info.isChecked());
        }
        adapter.notifyDataSetChanged();//更新界面
    }
    /**
     * 清理进程
     * @param view
     */
    public void clearProcess(View view){
        //在迭代时不能对被迭代对象进行增删操作，需重新创建集合数组后，把要操作的对象放入，在新集合数组中操作
        List<ProcessInfo> arrayList = new ArrayList<ProcessInfo>();
        long killSize = 0;
        int killCount = 0;
        for(ProcessInfo info:userList){
            if(info.isChecked()){
                arrayList.add(info);
            }
        }
        for(ProcessInfo info:systemList){
            if(info.isChecked()){
                arrayList.add(info);
            }
        }
        //操作新创建的集合
        if(arrayList.size()>0){
            for(ProcessInfo list:arrayList){
                killCount++;//杀死进程的个数
                killSize += list.getProcessSize()*1024;//总共释放多少内存
                freeStorage+=killSize;
                //把对象从集合中移除
                if(list.isSystem()){
                    systemList.remove(list);
                }else{
                    userList.remove(list);
                }
                //利用进程管理器杀死进程杀死进程
                ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                activityManager.killBackgroundProcesses(list.getProcessName());
            }
            adapter.notifyDataSetChanged();//更新界面
            tv_process_count.setText("运行中进程:" + (userList.size()+systemList.size()-killCount+1) + "个");//更新进行个数
            tv_ram.setText("剩余/总内存:" + Formatter.formatFileSize(this,freeStorage)+ "/" +
                    Formatter.formatFileSize(this, totalStorage));//更新内存大小

            Toast.makeText(this,"已杀死"+arrayList.size()+"个进程,共释放"+Formatter.formatFileSize(this,killSize)+"内存",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"没有进程可杀",Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 进程设置
     * @param view
     */
    public void setProcess(View view){
        Toast.makeText(this,"功能还未完善",Toast.LENGTH_SHORT).show();
    }
}
