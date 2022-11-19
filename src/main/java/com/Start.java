package com;

import com.bean.Folder;
import org.apache.commons.io.FileUtils;
import util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author chris
 * @create 2021/8/22
 */
public class Start {
    public static void main(String[] args) {
        //rename();
        listSize();
    }

    private static void rename() {
        String path = "C:\\File\\temporary";
        List<File> files = (List<File>) FileUtils.listFiles(new File(path), null, true);
        for (File f : files) {
            if (f.isFile()) {
                /**
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

                String originName = f.getName();
                String filename = originName;
                System.out.println(originName);
                if (originName.endsWith(".vdat")) {
                    filename = originName.replaceAll(".vdat", ".mp4");
                }
                if (originName.endsWith(".cnt")) {
                    filename = originName.replaceAll(".cnt", ".png");
                }

                if (originName.contains("K_") && originName.endsWith(".mp4")) {
                    //String filename = f.getName().replaceAll("(\\S{1,})m4a","");
                    String substring = originName;
                    try {
                        substring = originName.substring(originName.lastIndexOf('_'), originName.lastIndexOf('.'));
                        System.out.println(substring + "\r\n");
                    } catch (Exception e) {
                        System.out.println("ERROR" + originName);
                    }
                    filename = originName.replaceAll(substring, "");
                }
                System.out.println(filename + "\r\n");
                //对文件重命名
                File newFile = new File(path + File.separator + filename);
                f.renameTo(newFile);
                //输出文件改名前后变化
                //System.out.println(f.getName() + "==>" + newFile.getName());
            }
        }
    }

    /**
     * 对文件夹大小排序
     */
    private static void listSize() {
        //String path = "C:\\File\\temporary";
        String path = "C:\\File\\course";
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
    }
}
