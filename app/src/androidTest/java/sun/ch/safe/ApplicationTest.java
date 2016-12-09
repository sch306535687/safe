package sun.ch.safe;

import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;

import java.util.List;
import java.util.Random;

import sun.ch.bean.BlackNumberInfo;
import sun.ch.dao.BlackNameDao;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    private Context context;
    public ApplicationTest() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        this.context = getContext();
        super.setUp();
    }

    /**
     * 添加信息
     */
    public void testAdd(){
        BlackNameDao dao = new BlackNameDao(context);
        Random random = new Random();
        for(int i=0;i<50;i++){
            dao.add("15860061070"+i,(random.nextInt(3)+1)+"");
        }
    }

    /**
     * 测试所有信息
     */
    public void testAll(){
        BlackNameDao dao = new BlackNameDao(context);
        List<BlackNumberInfo> all = dao.getAll();
        for(BlackNumberInfo info:all){
            String number = info.getNumber();
            String mode = info.getMode();
            System.out.println("number:"+number+";mode:"+mode);
        }

    }
}