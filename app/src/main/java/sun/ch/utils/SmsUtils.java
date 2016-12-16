package sun.ch.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by sunch on 2016/12/14.
 *
 */
public class SmsUtils {
    /**
     * 定义一个接口供方法实现
     */
    public interface Progress{
        public abstract void setCount(int count);
        public abstract void setProgress(int process);
    }
    /**
     * 备份系统短信
     * @return
     */
    public static boolean getSms(Context context,Progress progress){

        //判断sd卡是否存在
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            try {
                File file = new File(Environment.getExternalStorageDirectory(), "message.xml");//保存的文件名为message.xml
                FileOutputStream outputStream = new FileOutputStream(file);
                //从系统内容提供者获取系统短信
                ContentResolver resolver = context.getContentResolver();
                Uri uri = Uri.parse("content://sms/");
                Cursor cursor = resolver.query(uri, new String[]{"address", "date", "body", "type"}, null, null, null);
                //使用序列化器把短信保存到message.xml
                XmlSerializer serializer = Xml.newSerializer();
                serializer.setOutput(outputStream,"utf-8");//执行写入方法
                serializer.startDocument("utf-8",true);
                serializer.startTag(null,"messages");
                int count = cursor.getCount();
                progress.setCount(count);
                int process = 0;
                while(cursor.moveToNext()){

                    serializer.startTag(null,"message");
                        serializer.startTag(null,"address");
                        serializer.text(cursor.getString(0));
                        serializer.endTag(null,"address");
                        serializer.startTag(null,"date");
                        serializer.text(cursor.getString(1));
                        serializer.endTag(null,"date");
                        serializer.startTag(null,"body");
                        serializer.text(cursor.getString(2));
                        serializer.endTag(null,"body");
                        serializer.startTag(null,"type");
                        serializer.text(cursor.getString(3));
                        serializer.endTag(null,"type");
                    serializer.endTag(null,"message");

                    process++;
                    SystemClock.sleep(1000);
                    progress.setProgress(process);
                }

                serializer.endTag(null,"messages");
                serializer.endDocument();
                outputStream.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
