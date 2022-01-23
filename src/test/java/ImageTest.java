import util.FastImageInfo;

import java.io.File;
import java.io.IOException;

/**
 * @author chris
 * @create 2022/1/23
 */
public class ImageTest {

    public static void main(String[] args) throws IOException {
        //FastImageInfo imageInfo = new FastImageInfo(file or byte or inputStream);
//        "C:/File/term/mmexport1623592736915.jpg"
        String path = "C:\\File\\term\\v2.ols100.1\\0\\tFMHpoPCEV2Rmjz_MAv9rjn8a-o.cnt";
        FastImageInfo imageInfo = new FastImageInfo(new File(path));
        //int width = imageInfo.getWidth();
        //int height = imageInfo.getHeight();
        System.out.println(imageInfo.toString());
    }
}
