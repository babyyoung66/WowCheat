package com.cinle.wowcheat.Service.Impl;

import com.cinle.wowcheat.Constants.FileConst;
import com.cinle.wowcheat.Exception.UploadFileException;
import com.cinle.wowcheat.Service.UploadServices;
import com.cinle.wowcheat.Utils.SecurityContextUtils;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @Author JunLe
 * @Time 2022/3/14 16:44
 */
@Service
public class UploadServicesImpl implements UploadServices {
    private  Logger log = LoggerFactory.getLogger(UploadServicesImpl.class);
    @Override
    public void uploadFile(Object target, String savePath) throws UploadFileException {
        MultipartFile file = (MultipartFile) target;
        Path path = Paths.get(FileConst.LOCAL_PATH + savePath);
        try {
            file.transferTo(path);

        } catch (IOException e) {
            log.info("文件上传失败,文件名: {},上传者: {},失败原因: {}", savePath, SecurityContextUtils.getCurrentUserUUID(), e.getMessage());
            // e.printStackTrace();
            throw new UploadFileException("文件上传失败！\n" + e.getMessage());
        }
    }

    @Override
    public void removeFile(String fileUrl) throws FileUploadException {
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
                log.info("删除文件失败,文件路径为: {},原因: {}", realPath, e.getMessage());
                throw new FileUploadException("文件删除失败！\n" + e.getMessage());
            }

        }
    }
}
