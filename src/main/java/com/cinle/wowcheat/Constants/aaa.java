package com.cinle.wowcheat.Constants;

import java.util.UUID;

/**
 * @Author JunLe
 * @Time 2022/3/5 22:20
 */
public class aaa {
    public static void main(String[] args) {
        String size = FileConst.SIZE_FILE;
        String unit = size.substring(size.length()-1);
        System.out.println("unit = " + unit);

        String name = "aaaa/dddd.png";
        String ExName = name.substring(name.lastIndexOf("."));       //获取文件后缀
        String uuName = UUID.randomUUID().toString();
        String filePath = "image/" + uuName +ExName;
        System.out.println("filePath = " + filePath);
    }
}
