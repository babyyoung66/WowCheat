package com.cinle.wowcheat.Model;

import com.cinle.wowcheat.Enum.FileType;

/**
 * @Author JunLe
 * @Time 2022/3/13 21:26
 * 文件信息，用于聊天信息
 */
public class FileDetail {
    private String fileUrl;
    private String fileName;
    private FileType fileType;

    public String getFileUrl() {
        return fileUrl;
    }

    public FileDetail setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public FileDetail setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public FileType getFileType() {
        return fileType;
    }

    public FileDetail setFileType(FileType fileType) {
        this.fileType = fileType;
        return this;
    }

    @Override
    public String toString() {
        return "FileDetail{" +
                "fileUrl='" + fileUrl + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                '}';
    }
}
