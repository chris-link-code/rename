package com;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chris
 * @create 2021/11/28
 */
public class Utils {
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
    private final static String SOURCE_PATH = "";
    /**
     * 源路径
     */
    private final static String TARGET_PATH = "";
    /**
     * 阻塞队列
     */
    private static LinkedBlockingQueue<File> queue = new LinkedBlockingQueue<>();
    /**
     * 线程池
     */
    private static ExecutorService executor = Executors.newFixedThreadPool(PROCESSOR);
    /**
     * 生产者计数
     */
    private static AtomicInteger produceCount = new AtomicInteger();

    private static CopyOnWriteArrayList<File> imageList = new CopyOnWriteArrayList<>();

    /**
     * 复制文件
     */
    public static void copyFile(File source, File dest)
            throws IOException {
        FileUtils.copyFile(source, dest);
    }

    /**
     * 下载图片
     * 利用阻塞队列LinkedBlockingQueue的生产者消费者模式
     */
    public static void downloadImageWithQueue() {
        // TODO: 2021/12/6
        //  查询优秀的生产者消费者模式代码

        File pathFile = new File(SOURCE_PATH);
        List<File> list = findFileList(pathFile, null);
        if (list == null || list.isEmpty()) {
            //LogUtil.e(SOURCE_PATH, "There are no picture");
            return;
        }
        //LogUtil.e("scan files", list.size());

        for (File file : list) {
            produceCount.incrementAndGet();

            // 过滤图片线程，生产者线程
            Runnable filterRunnable = () -> {
                if (isBigImage(file)) {
                    try {
                        queue.put(file);
                    } catch (InterruptedException e) {
                        //LogUtil.e(file.getAbsolutePath(), e.getMessage());
                    }
                }
            };

            // 复制图片线程，消费者线程
            Runnable copyRunnable = () -> {
                try {
                    /*
                     * 这里存在问题
                     * 总会出现消费者线程先执行,生产者线程后执行的情况
                     * 导致丢数据
                     */
                    if (produceCount.get() > 0) {
                        //LogUtil.e("produceCount", produceCount.get());
                        produceCount.decrementAndGet();
                        if (!queue.isEmpty()) {
                            File takeFile = queue.take();
                            String newPath = TARGET_PATH + takeFile.getName().replaceAll(".cnt", ".jpg");
                            File dest = new File(newPath);
                            Utils.copyFile(takeFile, dest);
                        }
                    }
                } catch (Exception e) {
//                    LogUtil.e(file.getAbsolutePath() + " copy error", e.getMessage());
                }
            };

            executor.execute(filterRunnable);
            executor.execute(copyRunnable);
        }
        executor.shutdown();

        try {
            boolean awaitTermination = executor.awaitTermination(30, TimeUnit.MINUTES);
//            LogUtil.e("ExecutorService awaitTermination", awaitTermination);
        } catch (InterruptedException e) {
//            LogUtil.e("ExecutorService termination interrupted", e.getMessage());
        }
//        LogUtil.e("download done", System.currentTimeMillis());
//        LogUtil.e("ExecutorService isTerminated", executor.isTerminated());
    }


    /**
     * 利用线程安全的List CopyOnWriteArrayList存File
     */
    public static void downloadImage() {
        filterImage();

        // IO密集型，可以使用较大的线程池，一般CPU核心数 * 2
        ExecutorService service = Executors.newFixedThreadPool(PROCESSOR * 2);
        if (imageList == null || imageList.isEmpty()) {
//            LogUtil.e(ApplicationProperties.imageCachePath, "There are no image to download");
            return;
        }
//        LogUtil.e("Need download image", imageList.size());
        for (File file : imageList) {
            Runnable runnable = () -> {
                try {
                    String newPath = SOURCE_PATH + file.getName().replaceAll(".cnt", ".jpg");
                    File dest = new File(newPath);
                    Utils.copyFile(file, dest);
                } catch (IOException e) {
//                    LogUtil.e(file.getAbsolutePath() + " copy error", e.getMessage());
                }
            };
            service.execute(runnable);
        }
        service.shutdown();
        try {
            boolean awaitTermination = service.awaitTermination(30, TimeUnit.MINUTES);
//            LogUtil.e("Download image ExecutorService awaitTermination", awaitTermination);
        } catch (InterruptedException e) {
//            LogUtil.e("Download image pool termination interrupted", e.getMessage());
        }
//        LogUtil.e("Download image done", System.currentTimeMillis());
//        LogUtil.e("Download image ExecutorService isTerminated", service.isTerminated());
    }

    /**
     * 过滤掉小图片
     */
    public static void filterImage() {
        File pathFile = new File(SOURCE_PATH);
        List<File> list = findFileList(pathFile, null);
        if (list == null || list.isEmpty()) {
//            LogUtil.e(ApplicationProperties.imageCachePath, "Scan no file");
            return;
        }
//        LogUtil.e("scan files", list.size());

        // 计算密集型，尽量使用较小的线程池，一般是CPU核心数+1
        ExecutorService service = Executors.newFixedThreadPool(PROCESSOR + 1);
        for (File file : list) {
            Runnable runnable = () -> {
                if (isBigImage(file)) {
                    imageList.add(file);
                }
            };
            service.execute(runnable);
        }
        service.shutdown();
        try {
            boolean awaitTermination = service.awaitTermination(30, TimeUnit.MINUTES);
            if (!awaitTermination) {
//                LogUtil.e("INFO", "Filter image ExecutorService awaitTermination is false");
            }
        } catch (InterruptedException e) {
//            LogUtil.e("Filter image pool termination interrupted", e.getMessage());
        }
//        LogUtil.e("filter done", System.currentTimeMillis());
    }

    /**
     * 递归遍历文件夹下的所有子文件
     *
     * @param path 目录
     * @param list 保存文件名的集合
     * @return
     */
    private static List<File> findFileList(File path, List<File> list) {
        if (!path.exists() || !path.isDirectory()) {
            return list;
        }
        if (list == null) {
            list = new ArrayList<>();
        }
        File[] files = path.listFiles();
        if (files == null || files.length < 1) {
            return null;
        }
        for (File file : files) {
            try {
                if (file.isDirectory()) {
                    // 回调自身继续查询
                    findFileList(file, list);
                } else {
                    list.add(file);
                }
            } catch (Exception e) {
//                LogUtil.e(file.getAbsolutePath() + " ERROR", e.getMessage());
            }
        }
        return list;
    }

    /**
     * 判断图片大小
     * 分辨率小于 200 * 200 将认定为小图
     */
    private static boolean isBigImage(File file) {
        boolean bigImage = false;
        if (file.isFile() && file.exists()) {
            try {
                /*
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                if (bitmap != null) {
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();

                    if (width > PIXEL && height > PIXEL) {
                        bigImage = true;
                        // LogUtil.e(file.getName(), "width: " + width + "\t height: " + height);
                    }
                }
                */
            } catch (Exception e) {
//                LogUtil.e(file.getAbsolutePath() + " ERROR", e.getMessage());
            }
        }
        return bigImage;
    }

    /**
     * 删除指定文件夹下所有文件
     */
    public static void deleteFile(File file) {
        if (file == null || !file.exists()) {
            //LogUtil.e("WARNING", "文件删除失败,请检查文件路径是否正确");
            return;
        }
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                deleteFile(f);
            } else {
                f.delete();
            }
        }
        file.delete();
    }
}