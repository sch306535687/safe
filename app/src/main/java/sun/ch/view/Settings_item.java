package sun.ch.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import sun.ch.safe.R;


/**
 * Created by sunch on 2016/11/30.
 */
public class Settings_item extends RelativeLayout {

    private static final String NAMASPACE = "http://schemas.android.com/ch.sun.activity";
    private TextView title;
    private TextView desc;
    private CheckBox check;
    private String settingTitle;
    private String desc_on;
    private String desc_off;

    public Settings_item(Context context) {
        super(context);
        initItem();
    }
    public Settings_item(Context context, AttributeSet attrs) {
        super(context, attrs);
        //获取自定义属性值
        settingTitle = attrs.getAttributeValue(NAMASPACE,"settingTitle");
        desc_on = attrs.getAttributeValue(NAMASPACE,"desc_on");
        desc_off = attrs.getAttributeValue(NAMASPACE,"desc_off");
        initItem();
    }
    public Settings_item(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initItem();
    }

    //初始化方法
    public void initItem(){
        //将自定义好的布局文件 R.layout.settings_item设置给当前的Settings_item
        View.inflate(getContext(), R.layout.settings_item,this);
        //获取属性
        title = (TextView) findViewById(R.id.tv_title);
        desc = (TextView) findViewById(R.id.tv_desc);
        check = (CheckBox) findViewById(R.id.check);
        //设置标题
        setTitle(settingTitle);
        //设置描述
        setDesc(desc_off);
    }
    //设置属性值
    public void setTitle(String title) {
        this.title.setText(title);
    }
    public void setDesc(String desc) {
        this.desc.setText(desc);
    }
    public void setCheck(boolean checked) {
        if(checked){
            setDesc(desc_on);
        }else{
            setDesc(desc_off);
        }
        this.check.setChecked(checked);
    }
    //获取checkBox值
    public boolean getCheck(){
        return check.isChecked();
    }

}
