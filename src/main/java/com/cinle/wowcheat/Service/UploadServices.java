package com.cinle.wowcheat.Service;

/**
 * @Author JunLe
 * @Time 2022/3/14 16:38
 */
public interface UploadServices {

    void uploadFile(Object target, String savePath) throws Exception;

    void removeFile(String fileUrl) throws Exception;
}
