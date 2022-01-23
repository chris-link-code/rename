package util;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author chris
 * @create 2022/1/22
 */
public class Utils {
    private final static Logger logger = LogManager.getLogger(Utils.class);

    /**
     * CPU线程数
     */
    private final static int PROCESSOR = Runtime.getRuntime().availableProcessors();
    /**
     * 像素
     */
    private final static int PIXEL = 600;
    /**
     * 源路径
     */
    private final static String SOURCE_PATH = "C:\\File\\term\\source\\";
    /**
     * 目标路径
     */
    private final static String TARGET_PATH = "C:\\File\\term\\target\\";
    private final static String DOT = ".";

    /**
     * 过滤掉小图片并下载
     */
    public static void downloadImage() {
        logger.info("Get start");
        File pathFile = new File(SOURCE_PATH);
        List<File> list = findFile(pathFile, null);
        if (list == null || list.isEmpty()) {
            logger.info("Scan no file");
            return;
        }
        logger.info("Scan files " + list.size());

        // 计算密集型，尽量使用较小的线程池，一般是CPU核心数+1
        ExecutorService service = Executors.newFixedThreadPool(PROCESSOR + 1);
        for (File file : list) {
            Runnable runnable = () -> filterImage(file);
            service.execute(runnable);
        }
        service.shutdown();
        try {
            boolean awaitTermination = service.awaitTermination(30, TimeUnit.MINUTES);
            logger.info("ExecutorService awaitTermination: " + awaitTermination);
        } catch (InterruptedException e) {
            logger.error("AwaitTermination interrupted", e);
        }
    }

    /**
     * 递归遍历文件夹下的所有子文件
     *
     * @param path 目录
     * @param list 保存文件名的集合
     * @return
     */
    private static List<File> findFile(File path, List<File> list) {
        if (!path.exists() || !path.isDirectory()) {
            return list;
        }
        File[] files = path.listFiles();
        if (files == null || files.length < 1) {
            return null;
        }
        if (list == null) {
            list = new CopyOnWriteArrayList<>();
        }
        for (File file : files) {
            try {
                if (file.isDirectory()) {
                    // 回调自身继续查询
                    findFile(file, list);
                } else {
                    list.add(file);
                }
            } catch (Exception e) {
                logger.error(file.getAbsolutePath(), e);
            }
        }
        return list;
    }

    /**
     * 判断图片大小
     * 分辨率小于 PIXEL * PIXEL 将认定为小图
     */
    private static void filterImage(@NotNull File file) {
        if (file.isFile() && file.exists()) {
            Lock lock = new ReentrantLock();
            lock.lock();
            try {
                FastImageInfo imageInfo = new FastImageInfo(file);
                int width = imageInfo.getWidth();
                int height = imageInfo.getHeight();
                //logger.info(file.getName() + imageInfo.toString());
                if (width < PIXEL && height < PIXEL) {
                    String fileName = file.getName();
                    String newPath;
                    if (fileName.contains(DOT)) {
                        //获取文件的后缀名
                        String suffix = fileName.substring(fileName.lastIndexOf(DOT));
                        newPath = TARGET_PATH +
                                fileName.replaceAll(DOT + suffix, DOT + imageInfo.getMimeType());
                    } else {
                        newPath = TARGET_PATH + fileName + DOT + imageInfo.getMimeType();
                    }
                    File dest = new File(newPath);
                    FileUtils.copyFile(file, dest);
                    //FileUtils.moveFile(file, dest);
                }
            } catch (IOException e) {
                logger.error(file.getAbsolutePath(), e);
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * 删除指定文件夹下所有文件
     */
    public static void deleteFile(File file) throws IOException {
        if (file == null || !file.exists()) {
            logger.error("File deletion failed, please check if the file path is correct");
            return;
        }
        if (file.isFile()) {
            FileUtils.delete(file);
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFile(f);
                } else {
                    FileUtils.delete(f);
                }
            }
            FileUtils.delete(file);
        }
    }
}