package com.cinle.wowcheat.Utils;

/**
 * @Author JunLe
 * @Time 2022/3/6 12:43
 */
public class DesensitizedUtil {

    public static String mobilePhone(String str){
        if (str == null || str.trim()=="" ||  str.length() < 6){
            return str;
        }
        String target = str.substring(2,str.length() -4);
        return str.replace(target,"*****");
    }

    public static String email(String str){
        if (str == null || str.trim()=="" || str.length() == 0){
            return str;
        }
        int index = str.indexOf("@");
        if (index < 0){
            return str;
        }
        if (index < 5){
           return  str.replace(str.substring(1,index),"******");
        }
        String target = str.substring(2,index -1);
        return str.replace(target,"******");
    }
}
