package sun.ch.utils;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by asus on 2016/11/28.
 */
public class StreamUtils {
    /**
     * 从流中读取为字符串
     * @param is 输入流
     * @return 返回字符串
     */
    public static String readFromStream(InputStream is) throws IOException {
        String resule = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int len = 0;
        byte[] buffer = new byte[1024];
        while( (len = is.read(buffer)) != -1 ){
            bos.write(buffer,0,len);
        }
        resule = bos.toString();
        return resule;
    }
    /**
     * 把assets目录中的数据库拷贝到/data/data/sun.ch.safe/files/下
     *
     * @param databaseName 数据库名称
     */
    public static void copyDatabase(String databaseName, Context context) {
        InputStream inputStream = null;
        FileOutputStream os = null;
        File file = new File(context.getFilesDir(),databaseName);//创建路径file对象
        //判断目录下是否已经存在此数据库
        if (file.exists()) {
            return;//如果存在就不需要读取
        }
        try {
            inputStream = context.getAssets().open(databaseName);//读取asset目录下下的数据库为输入流,注意，assets必须跟main目录同级
            os = new FileOutputStream(file);//创建输出流
            //开始拷贝
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = inputStream.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
