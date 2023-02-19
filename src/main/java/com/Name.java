package com;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import util.PinYinUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

        String word = "C:/File/temporary/word.txt";
        FileReader fileReader = new FileReader(word);
        String text = fileReader.readString();
        System.out.println(text);

//        System.out.println("There are " + text.length() + " words");
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

        String pinYin = "C:/File/temporary/pin_yin.txt";
        FileWriter pinYiniter = new FileWriter(pinYin);
        for (String s : list) {
            pinYiniter.write(s + "\t" + PinYinUtil.toPinyin(s) + "\r\n", true);
        }

        //可使用 Comparable 自定的规则进行排序，但汉字的排序效果不好
        //Collections.sort(list);
        String names = "C:/File/temporary/names.txt";
        FileWriter writer = new FileWriter(names);
        //AtomicInteger integer = new AtomicInteger();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                //System.out.println(list.get(i) + list.get(j));
                //System.out.println(list.get(j) + list.get(i));
                String name = "林" + list.get(i) + list.get(j) + ",";
//                System.out.println(name);
                writer.write(name, true);
                //fileWriter.write("林" + list.get(j) + list.get(i) + ",");
                /*int number = integer.incrementAndGet();
                if (number > 7) {
                    integer.set(0);
                }*/
            }
            writer.write("\r\n\r\n", true);
        }
    }
}
