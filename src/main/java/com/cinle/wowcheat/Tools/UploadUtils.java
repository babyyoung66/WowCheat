package com.cinle.wowcheat.Tools;

import com.cinle.wowcheat.Constants.FileConst;
import com.cinle.wowcheat.Enum.FileType;
import com.cinle.wowcheat.GlobalException.UploadFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @Author JunLe
 * @Time 2022/3/5 20:17
 */
public class UploadUtils {
    private static Logger log = LoggerFactory.getLogger(UploadUtils.class);

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
            return 1048576;
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

    public static void uploadFile(MultipartFile file, String filePath, FileType type) throws UploadFileException {
        Path path = Paths.get(FileConst.LOCAL_PATH + filePath);
        boolean exits = Files.exists(path);
        if (exits) {
            throw new UploadFileException("内部服务错误，请尝试重新上传！");
        }
        boolean checkSize = cheekSize(file.getSize(), type);
        if (!checkSize) {
            throw new UploadFileException(getLimitString(type) + "!");
        }
        try {
            file.transferTo(path);
        }catch (IOException e) {
            log.info("文件上传失败,文件名: {},上传者: {},失败原因: {}",filePath,SecurityContextUtils.getCurrentUserUUID(),e.getMessage());
           // e.printStackTrace();
            throw new UploadFileException("文件上传失败！" + e.getMessage());
        }
    }

    public static void removeFile(String filePath){
        if (!StringUtils.hasText(filePath)){
            return;
        }
        //将数据库存储的URL转换为本地URL
        String realPath = filePath.replace(FileConst.UPLOAD_BASE_PATH,FileConst.LOCAL_PATH);
        Path path = Paths.get(realPath);
        if (Files.exists(path)){
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    log.info("删除文件失败,文件路径为: {},原因: {}",realPath,e.getMessage());
                    e.printStackTrace();
                }

        }
    }
}
