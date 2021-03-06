package com.cinle.wowcheat.Constants;

/**
 * @Author JunLe
 * @Time 2022/3/5 20:08
 * 文件上传相关静态参数
 */
public class FileConst {
    private FileConst(){}

    /**
     * 外界访问根路径
     */
    public static final String ACCESS_PATH = "/files/";

    /**
     * 文件上传base地址
     * 外界访问的虚拟url
     * Nginx代理时请填写Nginx域名端口或ip端口
     */
    //public static final String UPLOAD_BASE_PATH = "http://127.0.0.1:9999" + ACCESS_PATH;
    //public static final String UPLOAD_BASE_PATH = "https://www.cinle.icu:1288" + ACCESS_PATH;
    public static final String UPLOAD_BASE_PATH = "https://www.cinle.icu" + ACCESS_PATH;


    /**
     * 头像另起一个url，因为www前缀的域名配置了cdn，cdn缓存30天
     * 导致修改头像后无法刷新显示，所以这里需要区别于其他文件
     * 让头像不走CND缓存
     */
    //public static final String Photo_BASE_PATH = "http://127.0.0.1:9999" + ACCESS_PATH;
    public static final String Photo_BASE_PATH = "https://cinle.icu" + ACCESS_PATH;

    /**
     * 文件上传对应的本地路径
     */
    public static final String LOCAL_PATH = System.getProperty("user.home") + "/WowCheat" + ACCESS_PATH;

    /**
     * 注册时默认头像路径
     */
    public static final String DEFAULT_PHOTO_URL = "./static/cheat.png";

    /**
     * 上传保存的物理目录
     */
    public static final String LOCAL_PHOTO_PATH = "image/";

    /**
     * 聊天文件存放根路径
     */
    public static final String CHEAT_FILES_PATH = "cheatFiles/";

    public static final String SIZE_IMAGE = "5M";

    public static final String SIZE_FILE = "200M";

    public static final String SIZE_VIDEO = "200M";

    public static final String SIZE_SOUND = "15M";

    /**
     * 文件名限制长度
     */
    public static final int NAME_LIMIT = 150;


}
