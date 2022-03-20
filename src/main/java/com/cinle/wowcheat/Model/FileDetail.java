package com.cinle.wowcheat.Model;

/**
 * @Author JunLe
 * @Time 2022/3/13 21:26
 */
public class FileDetail {
    private String fileUrl;
    private String fileName;
    private String fileType;

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

    public String getFileType() {
        return fileType;
    }

    public FileDetail setFileType(String fileType) {
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
