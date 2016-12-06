package sun.ch.utils;

import java.io.ByteArrayOutputStream;
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

}
