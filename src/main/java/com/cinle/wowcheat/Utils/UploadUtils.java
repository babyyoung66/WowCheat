package com.cinle.wowcheat.Utils;

import com.cinle.wowcheat.Constants.FileConst;
import com.cinle.wowcheat.Enum.FileType;
import com.cinle.wowcheat.Exception.UploadFileException;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @Author JunLe
 * @Time 2022/3/5 20:17
 */
public class UploadUtils {
    private UploadUtils() {
    }

    private static Logger log = LoggerFactory.getLogger(UploadUtils.class);
    private static List<String> ImageType = new ArrayList<>(Arrays.asList(".jpg",".jfif",".jpeg",".png","jpg",".tif",".gif",".svg",".bmp",".webp"));
    private static List<String> VideoType = new ArrayList<>(Arrays.asList(".avi",".wmv",".mpeg",".mp4",".m4v",".mov",".asf",".flv",".f4v",".rmvb",".mkv"));
    private static List<String> SoundType = new ArrayList<>(Arrays.asList(".cd",".mp3",".wave",".aiff",".mpeg",".midi",".wma",".vqf",".arm",".ape",".aac",".amr",".ra",".ram"));


    private static boolean cheekSize(long fileSize, FileType type) {
        long size = getLimitSizeByType(type);
        return fileSize > size ? false : true;
    }

    private static long getLimitSizeByType(FileType type) {
        switch (type) {
            case file: {
                String size = FileConst.SIZE_FILE;
                String unit = size.substring(size.length() - 1);
                return ComputeLimitSize(unit);
            }
            case image: {
                String size = FileConst.SIZE_IMAGE;
                String unit = size.substring(size.length() - 1);
                return ComputeLimitSize(unit);
            }
            case sound: {
                String size = FileConst.SIZE_SOUND;
                String unit = size.substring(size.length() - 1);
                return ComputeLimitSize(unit);
            }
            case video: {
                String size = FileConst.SIZE_VIDEO;
                String unit = size.substring(size.length() - 1);
                return ComputeLimitSize(unit);
            }
        }
        return 0;
    }

    private static long ComputeLimitSize(String unit) {
        if ("K".equals(unit.toUpperCase())) {
            return 1024;
        } else if ("M".equals(unit.toUpperCase())) {
            return 10485760;
        } else if ("G".equals(unit.toUpperCase())) {
            return 1073741824;
        }
        return 0;
    }

    private static String getLimitString(FileType type) {
        String common = "限制文件最大为：";
        switch (type) {
            case file: {
                return common + FileConst.SIZE_FILE;
            }
            case image: {
                return common + FileConst.SIZE_IMAGE;
            }
            case sound: {
                return common + FileConst.SIZE_SOUND;
            }
            case video: {
                return common + FileConst.SIZE_VIDEO;
            }
        }
        return "系统不允许该类型文件上传";
    }

    /**
     * @param file
     * @param filePath
     * @param type
     * @return 真实保存地址
     * @throws UploadFileException
     */
    public static String uploadFile(MultipartFile file, String filePath, FileType type) throws UploadFileException {
        if (file == null || file.getSize() == 0){
            throw new UploadFileException("请选择文件！");
        }
        String realPath = FileConst.LOCAL_PATH + filePath;
        Path path = Paths.get(realPath);
        boolean checkSize = cheekSize(file.getSize(), type);
        if (!checkSize) {
            throw new UploadFileException(getLimitString(type) + "!");
        }
        try {
            if (path.toFile().exists()){
                Files.delete(path);
            }
            //父目录不存在则创建父目录
            if(!path.toFile().getParentFile().exists()){
                path.toFile().getParentFile().mkdirs();
            }
            file.transferTo(path);
            return realPath;
        } catch (Exception e) {
            String error = e.getMessage();
            if (e instanceof NoSuchFileException){
                error = "文件或文件路径不存在!";
            }
            log.error("文件上传失败,文件名: {},上传者: {},失败原因: {}", file.getOriginalFilename(), SecurityContextUtils.getCurrentUserUUID(), error);
            e.printStackTrace();
            throw new UploadFileException("文件上传失败！\n" + error);
        }
    }

    public static void removeFile(String fileUrl) {
        if (!StringUtils.hasText(fileUrl)) {
            return;
        }
        //将数据库存储的URL转换为本地URL
        String realPath = fileUrl.replace(FileConst.UPLOAD_BASE_PATH, FileConst.LOCAL_PATH);
        Path path = Paths.get(realPath);
        if (Files.exists(path)) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                e.printStackTrace();
                log.error("删除文件失败,文件路径为: {},原因: {}", realPath, e.getMessage());
                //throw new FileUploadException("文件删除失败！\n" + e.getMessage());
            }

        }
    }

    public static boolean checkFileType(MultipartFile file,FileType type) throws UploadFileException {
        if (file == null){
            throw new UploadFileException("请选择文件！");
        }
        String name = file.getOriginalFilename();
        String suffixName = name.substring(name.lastIndexOf("."));       //获取文件后缀
        String suffix = suffixName.toLowerCase();
        if (FileType.image.equals(type)){
            return ImageType.contains(suffix);
        }
        if (FileType.video.equals(type)){
            return VideoType.contains(suffix);
        }
        if (FileType.sound.equals(type)){
            return SoundType.contains(suffix);
        }
        if (FileType.file.equals(type)){
            return true;
        }
        return false;
    }

    public static String getCheatFilesPath(String uuid,String fileName){
        return FileConst.CHEAT_FILES_PATH + uuid+ "/" + fileName;
    }
}
