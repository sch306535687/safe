package sun.ch.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.MessageDigest;

/**
 * Created by sunch on 2016/11/2.
 */
public class md5 {

    /**
     * 计算密码md5值
     * @param pwd
     * @return
     */
    public final static String MD5(String pwd) {
        //用于加密的字符
        char md5String[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            //使用平台的默认字符集将此 String 编码为 byte序列，并将结果存储到一个新的 byte数组中
            byte[] btInput = pwd.getBytes();

            //信息摘要是安全的单向哈希函数，它接收任意大小的数据，并输出固定长度的哈希值。
            MessageDigest mdInst = MessageDigest.getInstance("MD5");

            //MessageDigest对象通过使用 update方法处理数据， 使用指定的byte数组更新摘要
            mdInst.update(btInput);

            // 摘要更新之后，通过调用digest（）执行哈希计算，获得密文
            byte[] md = mdInst.digest();

            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {   //  i = 0
                byte byte0 = md[i];  //95
                str[k++] = md5String[byte0 >>> 4 & 0xf];    //    5
                str[k++] = md5String[byte0 & 0xf];   //   F
            }

            //返回经过加密后的字符串
            return new String(str);

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据应用源地址计算md5值
     *
     * @return
     */
    public static String getAppMd(String sourceDir) {
        String result = null;
        try {
            File file = new File(sourceDir);
            //获取数字摘要
            MessageDigest messageDigest = MessageDigest.getInstance("md5");
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                messageDigest.update(buffer, 0, len);
            }
            byte[] bytes = messageDigest.digest();//获取读取到的字节数组
            //把字节数组转成字符串
            StringBuffer sb = new StringBuffer();
            for (byte b : bytes) {
                int number = b & 0xff;
                String hex = Integer.toHexString(number);
                if (hex.length() == 1) {
                    sb.append("0"+hex);
                } else {
                    sb.append(hex);
                }
            }
            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
