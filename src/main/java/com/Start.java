package com;

import cn.hutool.crypto.digest.DigestUtil;
import com.bean.Folder;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender;
import util.Utils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author chris
 * @create 2021/8/22
 */
public class Start {
    public static void main(String[] args) {
        try {
            rename();
//            listFolder();
//            move();
//            moveSameFile();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 文件重命名
     */
    private static void rename() {
//        String path = "D:\\thunder\\kite";
//        String path = "E:\\video\\苍兰诀";
        String path = "D:\\thunder\\亮剑";
//        String path = "/Volumes/Disk/video/山河令/";
        List<File> files = (List<File>) FileUtils.listFiles(new File(path), null, true);
        for (File f : files) {
            if (f.isFile()) {
                /*
                 //正则判断是否含有dy2018标识
                 if (f.getName().matches("\\S*dy2018\\S*")) {

                 StringBuilder filename = new StringBuilder();

                 int begin = f.getName().indexOf('[');
                 int end = f.getName().indexOf(']');

                 //begin不等于0说明标识不再文件名的开头
                 if (begin != 0) {
                 filename.append(f.getName().substring(0, begin - 1));
                 } else {
                 filename.append(f.getName().substring(0, begin));
                 }
                 //此时说明标识在文件名的最后，紧跟着文件扩展名
                 if (end == f.getName().lastIndexOf(".") - 1) {
                 //注意此时截取字符串开始索引是end+1,因为‘]’之后的点是跟扩展名一起的。文件名"幕.rmvb"会变成"幕rmvb"
                 filename.append(f.getName().substring(end + 1));
                 } else {
                 //这时文件名中会有一个多余的点字符，要去掉
                 filename.append(f.getName().substring(end + 2));
                 }

                 String filename = f.getName().replaceAll("vdat","mp4");
                 //对文件重命名
                 File newFile = new File(f.getParent() + File.separator + filename.toString());
                 f.renameTo(newFile);
                 //输出文件改名前后变化
                 System.out.println(f.getName() + "==>" + newFile.getName());
                 }*/

                String filename = f.getName();
//                String filename = filename;
//                System.out.println(filename);
                if (filename.endsWith(".vdat")) {
                    filename = filename.replaceAll(".vdat", ".mp4");
                }
                if (filename.endsWith(".cnt")) {
                    filename = filename.replaceAll(".cnt", ".png");
                }
                // 去空格
                if (filename.contains(" ")) {
                    filename = filename.replaceAll("\\s+", "_");
                }

                if (filename.contains("K_") && filename.endsWith(".mp4")) {
                    //String filename = f.getName().replaceAll("(\\S{1,})m4a","");
                    String substring = filename;
                    try {
                        substring = filename.substring(filename.lastIndexOf('_'), filename.lastIndexOf('.'));
//                        System.out.println(substring + "\r\n");
                    } catch (Exception e) {
                        System.out.println("ERROR" + filename);
                    }
                    filename = filename.replaceAll(substring, "");
                }
//                System.out.println(filename + "\r\n");
                //对文件重命名
//                filename = "风筝_" + filename;
                String[] split = filename.split("\\.");
//                char[] split = filename.toCharArray();
//                filename = "_" + split[3] + split[4] + "." + filename.split("\\.")[1];
//                filename = "岁月_" + filename;
//                filename = "山海情_" + split[3] + ".mp4";
                filename = split[0] + "_" + split[1] + "." + split[6];
//                filename = "激情燃烧的岁月" + "_" + split[2].replaceAll("\\D+", "") + "." + split[3];
                filename = filename.replaceAll("E", "");
                File newFile = new File(path + File.separator + filename);
                // 重命名
                f.renameTo(newFile);
                System.out.println(f.getName() + " --> " + newFile.getName());
            }
        }
    }

    /**
     * 对文件夹大小排序
     */
    private static void listFolder() {
        long start = System.currentTimeMillis();
//        String path = "D:\\code\\rust";
        //String path = "C:\\File\\course";
        //String path = "C:\\Users\\chris\\AppData\\Local\\Packages";
//        String path = "D:\\develop\\mysql\\data";
        String path = "D:\\develop\\repository";
        //List<File> files = (List<File>) FileUtils.listFiles(new File(path), null, false);
        File currentPath = new File(path);
        File[] files = currentPath.listFiles();
        List<Folder> folderList = new ArrayList<>(1024);
        for (File file : files) {
            /*if (file.isFile()) {
                System.out.println(file.getName() + " is file: " + Utils.sizeTransfer(file.length()));
            }*/
            // 只要文件夹
            if (file.isDirectory()) {
                folderList.add(new Folder(file.getName(), FileUtils.sizeOfDirectory(file)));
                //System.out.println(file.getName() + " is directory: " + Utils.sizeTransfer(FileUtils.sizeOfDirectory(file)));
            }
        }

        folderList.sort(Comparator.comparingLong(Folder::getSize));

        for (int i = folderList.size(); i > 0; i--) {
            Folder folder = folderList.get(i - 1);
            System.out.println(Utils.sizeTransfer(folder.getSize()) + " \t: " + folder.getName());
        }
        long end = System.currentTimeMillis();
        System.out.println("spend " + (end - start) + " ms");
    }

    /**
     * 判断相同文件并移动
     */
    private static void moveSameFile() throws InterruptedException {
        long start = System.currentTimeMillis();
        Map<String, File> map = new ConcurrentHashMap<>(1 << 16);
        String path = "D:\\pron";
//        String path = "D:\\picture\\temp";
        Collection<File> files = FileUtils.listFiles(new File(path), null, true);
        System.out.println("files: " + files.size());
        //        files.forEach(file -> map.put(file.getAbsolutePath(), file));
        String zero = "D:\\temporary\\0";
        String one = "D:\\temporary\\1";

        // 线程池
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        files.forEach(file -> {
            Runnable runnable = () -> {
                // 根据文件名判断
                String filename = file.getName();
                // 根据文件MD5值判断 md5Hex,sha1Hex,sha256Hex,sha512Hex
//                String key = DigestUtil.sha256Hex(file);
                String key = String.valueOf(file.length());
                if (map.containsKey(key)) {
                    String targetZero = zero + "\\" + filename;
                    String targetOne = one + "\\" + filename;
                    try {
                        File mapFile = map.get(key);

                        System.out.println(file.getAbsoluteFile());
                        System.out.println(mapFile.getAbsoluteFile());

                        if (mapFile.exists()) {
//                            FileUtils.moveFile(mapFile, new File(targetOne));
                        }
//                        FileUtils.moveFile(file, new File(targetZero));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    map.put(key, file);
                }
            };
            executor.execute(runnable);
        });
        executor.shutdown();
        boolean terminate = executor.awaitTermination(30, TimeUnit.MINUTES);
        long end = System.currentTimeMillis();
        System.out.println("time: " + (end - start) + "ms");
    }

    /**
     * 移动文件
     */
    private static void move() throws IOException {
        String path = "/Volumes/Disk/video/";
        String target = "/Volumes/Disk/video/大宋提刑官/";
//        List<File> files = (List<File>) FileUtils.listFiles(new File(path), null, false);
        File[] files = new File(path).listFiles();
        for (File file : files) {
            if (file.isDirectory() && file.getName().endsWith(".mp4")) {
//                System.out.println(file.getAbsoluteFile());
                for (File f : file.listFiles()) {
                    if (f.isFile() && f.getName().endsWith(".mp4")) {
//                        System.out.println(f.getAbsoluteFile());
                        File targetFile = new File(target + f.getName());
                        FileUtils.moveFile(f, targetFile);
                    }
                }
            }
        }
    }
}
