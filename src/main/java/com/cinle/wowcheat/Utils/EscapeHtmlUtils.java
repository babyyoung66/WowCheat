package com.cinle.wowcheat.Utils;

/**
 * @Author JunLe
 * @Time 2022/3/25 0:57
 */
public class EscapeHtmlUtils {
    public static String escapeHtml(String input){
        return input.replace("<","&lt").replace(">","&gt");
    }
}
