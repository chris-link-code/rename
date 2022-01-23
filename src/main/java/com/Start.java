package com;

import java.io.File;

/**
 * @author chris
 * @create 2021/8/22
 */
public class Start {
    public static void main(String[] args) {
        File file = new File("D:/term");
        File[] files = file.listFiles();
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
                File newFile = new File(f.getParent() + File.separator + filename);
                f.renameTo(newFile);
                //输出文件改名前后变化
                //System.out.println(f.getName() + "==>" + newFile.getName());
            }
        }

    }
}
