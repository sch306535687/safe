package sun.ch.fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sun.ch.bean.Info;
import sun.ch.dao.AppLockDao;
import sun.ch.safe.R;
import sun.ch.utils.AppInfos;

/**
 * Created by sunch on 2016/12/21.
 */
public class LockFragment extends Fragment {
    private AppLockDao dao;
    private AppInfos app;
    private View view;
    private ListView listview;
    private List<Info> lockList;
    private TextView unlock_count;
    private MyLockAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_lock, null);
        listview = (ListView) view.findViewById(R.id.listview);
        unlock_count = (TextView) view.findViewById(R.id.lock_count);
        dao = new AppLockDao(getActivity());
        app = new AppInfos();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        new Thread(){
            @Override
            public void run() {
                List<Info> appInfos = app.getAppInfos(getActivity());
                lockList = new ArrayList<>();
                for (Info info:appInfos){
                    boolean b = dao.search(info.getPackAgeName());
                    if(b){
                        lockList.add(info);
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new MyLockAdapter();
                        listview.setAdapter(adapter);
                    }
                });
            }
        }.start();
    }
    private class MyLockAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            unlock_count.setText("未加锁软件("+lockList.size()+")个");
            return lockList.size();
        }

        @Override
        public Object getItem(int position) {
            return lockList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null ) {
                convertView = View.inflate(getActivity(), R.layout.unlock_items, null);
                holder = new ViewHolder();
                holder.app_name = (TextView) convertView.findViewById(R.id.app_name);
                holder.app_icon = (ImageView) convertView.findViewById(R.id.app_icon);
                holder.app_lock = (ImageView) convertView.findViewById(R.id.app_lock);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final Info info = lockList.get(position);
            holder.app_icon.setBackgroundDrawable(info.getAppIcon());
            holder.app_name.setText(info.getAppName());
            //监听
            final View finalConvertView = convertView;
            holder.app_lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //定义动画
                    TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1.0f,
                            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                    translateAnimation.setDuration(2000);
                    finalConvertView.startAnimation(translateAnimation);
                    new Thread(){
                        @Override
                        public void run() {
                            SystemClock.sleep(2000);
                            lockList.remove(info);//从集合中删除
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    boolean b = dao.delete(info.getPackAgeName());//添加到加锁数据库
                                    if(b){
                                        adapter.notifyDataSetChanged();//刷新界面
                                    }
                                }
                            });
                        }
                    }.start();

                }
            });
            return convertView;
        }
    }

    private class ViewHolder {
        TextView app_name;
        ImageView app_icon;
        ImageView app_lock;
    }
}
