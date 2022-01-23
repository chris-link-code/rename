package util;

import java.io.*;

/**
 * @author chris
 * @create 2022/1/23
 * 快速获取图片的大小
 * Java提供ImageIO.read方法获取包含图片大小、尺寸宽高等数据的BufferedImage对象，
 * 但它需要把图片完全加载到内存中，对于某些只想要图片宽高信息的场景来说，这样会更占内存，效率较低。
 * 下面提供另外一种性能更好的方式，根据图片字节数组获取图片宽高
 * https://www.cnblogs.com/xiaona/p/13869504.html
 */
public class FastImageInfo {
    private int height;
    private int width;
    private String mimeType;

    public FastImageInfo(File file) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            processStream(is);
        }
    }

    public FastImageInfo(InputStream is) throws IOException {
        processStream(is);
    }

    public FastImageInfo(byte[] bytes) throws IOException {
        try (InputStream is = new ByteArrayInputStream(bytes)) {
            processStream(is);
        }
    }

    private void processStream(InputStream is) throws IOException {
        int c1 = is.read();
        int c2 = is.read();
        int c3 = is.read();

        mimeType = null;
        width = height = -1;

        // GIF
        if (c1 == 'G' && c2 == 'I' && c3 == 'F') {
            is.skip(3);
            width = readInt(is, 2, false);
            height = readInt(is, 2, false);
            mimeType = "gif";

            // JPG
        } else if (c1 == 0xFF && c2 == 0xD8) {
            while (c3 == 255) {
                int marker = is.read();
                int len = readInt(is, 2, true);
                if (marker == 192 || marker == 193 || marker == 194) {
                    is.skip(1);
                    height = readInt(is, 2, true);
                    width = readInt(is, 2, true);
                    mimeType = "jpeg";
                    break;
                }
                is.skip(len - 2);
                c3 = is.read();
            }

            // PNG
        } else if (c1 == 137 && c2 == 80 && c3 == 78) {
            is.skip(15);
            width = readInt(is, 2, true);
            is.skip(2);
            height = readInt(is, 2, true);
            mimeType = "png";

            // BMP
        } else if (c1 == 66 && c2 == 77) {
            is.skip(15);
            width = readInt(is, 2, false);
            is.skip(2);
            height = readInt(is, 2, false);
            mimeType = "bmp";

            // WEBP
        } else if (c1 == 'R' && c2 == 'I' && c3 == 'F') {
            byte[] bytes = new byte[27];
            is.read(bytes);
            width = ((int) bytes[24] & 0xff) << 8 | ((int) bytes[23] & 0xff);
            height = ((int) bytes[26] & 0xff) << 8 | ((int) bytes[25] & 0xff);
            mimeType = "webp";
        } else {
            int c4 = is.read();
            //TIFF
            if ((c1 == 'M' && c2 == 'M' && c3 == 0 && c4 == 42)
                    || (c1 == 'I' && c2 == 'I' && c3 == 42 && c4 == 0)) {
                boolean bigEndian = c1 == 'M';
                int ifd = 0;
                int entries;
                ifd = readInt(is, 4, bigEndian);
                is.skip(ifd - 8);
                entries = readInt(is, 2, bigEndian);
                for (int i = 1; i <= entries; i++) {
                    int tag = readInt(is, 2, bigEndian);
                    int fieldType = readInt(is, 2, bigEndian);
                    int valOffset;
                    if ((fieldType == 3 || fieldType == 8)) {
                        valOffset = readInt(is, 2, bigEndian);
                        is.skip(2);
                    } else {
                        valOffset = readInt(is, 4, bigEndian);
                    }
                    if (tag == 256) {
                        width = valOffset;
                    } else if (tag == 257) {
                        height = valOffset;
                    }
                    if (width != -1 && height != -1) {
                        mimeType = "tiff";
                        break;
                    }
                }
            }
        }
        if (mimeType == null) {
            throw new IOException("Unsupported image type");
        }
    }

    private int readInt(InputStream is, int noOfBytes, boolean bigEndian) throws IOException {
        int ret = 0;
        int sv = bigEndian ? ((noOfBytes - 1) * 8) : 0;
        int cnt = bigEndian ? -8 : 8;
        for (int i = 0; i < noOfBytes; i++) {
            ret |= is.read() << sv;
            sv += cnt;
        }
        return ret;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public String toString() {
        return "MIME Type : " + mimeType + "\t Width : " + width
                + "\t Height : " + height;
    }
}

