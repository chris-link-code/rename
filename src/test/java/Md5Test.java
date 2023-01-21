import cn.hutool.crypto.SecureUtil;

import java.io.File;

/**
 * @author chris
 * @create 2022/11/30
 */
public class Md5Test {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        File file = new File("C:/File/temporary/s.rmvb");
        String md5 = SecureUtil.md5(file);
        System.out.println(md5);
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
