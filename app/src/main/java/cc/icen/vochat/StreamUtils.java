package cc.icen.vochat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Tian on 2016/7/13.
 */
public class StreamUtils {
    /**
     * 将字节流转化为字符串,使用 Android 默认编码
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static String inputStream2String(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len = -1;
        byte[] buffer = new byte[1024];
        while ((len = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        inputStream.close();
        return new String(baos.toByteArray());
    }
}
