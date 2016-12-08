package sun.ch.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import sun.ch.safe.R;


/**
 * Created by sunch on 2016/11/30.
 */
public class Settings_click extends RelativeLayout {

    private TextView desc;
    private TextView title;

    public Settings_click(Context context) {
        super(context);
        initItem();
    }
    public Settings_click(Context context, AttributeSet attrs) {
        super(context, attrs);
        initItem();
    }
    public Settings_click(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initItem();
    }

    //初始化方法
    public void initItem(){
        //将自定义好的布局文件 R.layout.settings_item设置给当前的Settings_item
        View.inflate(getContext(), R.layout.settings_click,this);
        //获取属性
        desc = (TextView) findViewById(R.id.tv_desc);
        title = (TextView) findViewById(R.id.tv_title);

    }
    //设置属性值
    public void setDesc(String desc) {
        this.desc.setText(desc);
    }
    public void setTitle(String title) {
        this.title.setText(title);
    }

}
