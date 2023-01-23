package com;

import util.PinYinUtil;
import util.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * @author chris
 * @create 2023/1/21
 * <p>
 * Get name for baby
 */
public class Name {
    public static void main(String[] args) throws IOException {
        /*String text = "大为子中生国里自会家可天然于心发当成道想开现前些所同意方分法知" +
                "高其常正见明问力理尔文几定本相果月实向全信气并新才夫书水主界海德克先由" +
                "安写光望乐更东应直字平关至告万风原通立远士呢达深清业思非元飞言干欢合" +
                "必交及则品近司奇未且台青久周步希亚兴容极首式照强石古华米节故历示史准基" +
                "志义竟单兰念易居图专灵存习务怀广苏显查参亮致阳严支普展意贯思致志导质若" +
                "贝光元圣阳午章坚云吉临卫慧智禾才众益合云采景余承项革良宣怀康杰盖亚凡升" +
                "平介至志知业启志强超毅华胜成荣德才迎来飞宇云文仁宏益明伟光思广兴章承元";*/
        //String text = "章承元元";

        File file = new File("C:/File/temporary/word.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        StringBuffer sb = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        String text = sb.toString();
        //System.out.println(text);

        System.out.println("There are " + text.length() + " words");
        HashSet<String> unique = new HashSet<>(256);
        char[] chars = text.toCharArray();
        for (char c : chars) {
            unique.add(Character.toString(c));
        }
        List<String> list = new ArrayList<>(256);
        StringBuffer uniqueWord = new StringBuffer();
        for (String s : unique) {
            list.add(s);
            uniqueWord.append(s);
        }
        int size = list.size();
        System.out.println("There are " + size + " unique words");
        System.out.println(uniqueWord);

        File pinYinFile = new File("C:/File/temporary/pin_yin.txt");
        FileWriter pinYiniter = new FileWriter(pinYinFile);
        for (String s : list) {
            pinYiniter.write(s + "\t" + PinYinUtil.toPinyin(s) + "\r\n");
        }
        pinYiniter.flush();
        pinYiniter.close();

        //可使用 Comparable 自定的规则进行排序，但汉字的排序效果不好
        //Collections.sort(list);
        File writeFile = new File("C:/File/temporary/names.txt");
        FileWriter fileWriter = new FileWriter(writeFile);
        AtomicInteger integer = new AtomicInteger();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                //System.out.println(list.get(i) + list.get(j));
                //System.out.println(list.get(j) + list.get(i));
                fileWriter.write("林" + list.get(i) + list.get(j) + ",");
                fileWriter.write("林" + list.get(j) + list.get(i) + ",");
                int number = integer.incrementAndGet();
                if (number > 0) {
                    fileWriter.write("\r\n");
                    integer.set(0);
                }
            }
        }
        fileWriter.flush();
        fileWriter.close();
    }
}
