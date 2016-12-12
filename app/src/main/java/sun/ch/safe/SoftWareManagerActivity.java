package sun.ch.safe;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

import sun.ch.bean.Info;
import sun.ch.utils.AppInfos;

/**
 * Created by Administrator on 2016/12/12.
 */
public class SoftWareManagerActivity extends Activity {

    private ListView listView;
    private List<Info> infos;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //填充listview数据
            AppAdapter adapter = new AppAdapter();
            listView.setAdapter(adapter);
        }
    };
    private List<Info> systemList;
    private List<Info> userList;
    private Info info;
    private TextView app_type;
    private PopupWindow pop;
    private View popview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_softwaremanager);

        listView = (ListView) findViewById(R.id.listview);
        app_type = (TextView) findViewById(R.id.app_type);
        //获取所有应用数据
        new Thread() {
            @Override
            public void run() {
                AppInfos appInfos = new AppInfos();
                infos = appInfos.getAppInfos(SoftWareManagerActivity.this);
                systemList = new ArrayList<Info>();
                userList = new ArrayList<Info>();
                for (Info info : infos) {
                    boolean systemApp = info.isSystemApp();
                    if (systemApp) {
                        systemList.add(info);
                    } else {
                        userList.add(info);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();

        //监听滚动事件
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                dismissPop();//退出popwindow
                if(firstVisibleItem!=0){//注意判断
                    if (firstVisibleItem < userList.size() + 1) {
                        app_type.setText("我的应用");
                    } else {
                        app_type.setText("系统应用");
                    }
                }
            }

        });

        //监听点击条目事件，弹出popwindow
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popview = View.inflate(SoftWareManagerActivity.this, R.layout.popwindow_items, null);
                dismissPop();
                pop = new PopupWindow(popview, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                pop.setBackgroundDrawable(new BitmapDrawable());
                //获取view展示到窗体上的位置
                int[] location = new int[2];
                view.getLocationInWindow(location);

                pop.showAtLocation(parent, Gravity.LEFT+Gravity.TOP,65,location[1]);
                AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 1);//透明动画
                alphaAnimation.setDuration(1000);
                ScaleAnimation scaleAnimation = new ScaleAnimation(0.5f, 1, 0.5f, 1,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);//缩放动画
                scaleAnimation.setDuration(600);
                popview.startAnimation(scaleAnimation);//开始动画
            }
        });
    }

    /**
     * 退出popwindow
     */
    private void dismissPop() {
        if(pop!=null&&pop.isShowing()){
            pop.dismiss();
            pop = null;
        }
    }

    private class AppAdapter extends BaseAdapter {

        private ViewHolder holder;

        @Override
        public int getCount() {
            return infos.size() + 2;
        }

        @Override
        public Object getItem(int position) {
            return infos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                TextView textView = new TextView(SoftWareManagerActivity.this);
                textView.setText("我的应用");
                textView.setTextSize(18);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            } else if (position == (userList.size() + 1)) {
                TextView textView = new TextView(SoftWareManagerActivity.this);
                textView.setText("系统应用");
                textView.setTextSize(18);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            }

            if (position < (userList.size() + 1)) {
                info = userList.get(position - 1);
            } else {
                info = systemList.get(position - (userList.size() + 2));
            }


            if (convertView != null && convertView instanceof LinearLayout) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(SoftWareManagerActivity.this, R.layout.appinfos_items, null);
                holder = new ViewHolder();
                holder.app_name = (TextView) convertView.findViewById(R.id.app_name);
                holder.app_size = (TextView) convertView.findViewById(R.id.app_size);
                holder.app_storage = (TextView) convertView.findViewById(R.id.app_storage);
                holder.app_icon = (ImageView) convertView.findViewById(R.id.app_icon);
                convertView.setTag(holder);
            }
            holder.app_name.setText(info.getAppName());
            holder.app_size.setText(Formatter.formatFileSize(SoftWareManagerActivity.this, info.getAppSize()));
            holder.app_icon.setBackgroundDrawable(info.getAppIcon());
            boolean sdCard = info.isSDCard();
            if (sdCard) {
                holder.app_storage.setText("sd卡");
            } else {
                holder.app_storage.setText("手机内存");
            }

            return convertView;
        }
    }

    private class ViewHolder {
        TextView app_name;
        TextView app_size;
        TextView app_storage;
        ImageView app_icon;
    }

    @Override
    protected void onDestroy() {
        dismissPop();//当退出当前activity时，退出popwindow
        super.onDestroy();
    }
}
