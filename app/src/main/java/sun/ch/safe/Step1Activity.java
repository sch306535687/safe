package sun.ch.safe;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Administrator on 2016/12/1.
 */
public class Step1Activity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step1);
    }

    @Override
    public void nextPage() {
        //跳转到第二步
        startActivity(new Intent(this,Step2Activity.class));
        //设置切换动画，从右边进入，左边退出
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
    }

    @Override
    public void previousPage() {

    }

}
