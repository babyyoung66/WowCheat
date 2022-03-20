package com.cinle.wowcheat.Constants;

/**
 * @Author JunLe
 * @Time 2022/3/5 20:08
 * 文件上传相关静态参数
 */
public class FileConst {

    /**
     * 外界访问根路径
     */
    public static final String ACCESS_PATH = "/files/";

    /**
     * 文件上传base地址
     */
    public static final String UPLOAD_BASE_PATH = "http://127.0.0.1:8080" + ACCESS_PATH;


    /**
     * 文件上传对应的本地路径
     */
    public static final String LOCAL_PATH = System.getProperty("user.home") + "/WowCheat" + ACCESS_PATH;

    public static final String SIZE_IMAGE = "10M";

    public static final String SIZE_FILE = "50M";

    public static final String SIZE_VIDEO = "200M";

    public static final String SIZE_SOUND = "30M";
}
