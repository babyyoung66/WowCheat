package com.cinle.wowcheat.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * wow_userfile_detail
 * @author
 * 记录用户上传的文件信息
 */

public class UserFileDetail implements Serializable {
    private int autoId;

    private String uuid;

    private String filePath;

    private String fileName;

    private String fileType;

    private Date uploadTime;

    public int getAutoId() {
        return autoId;
    }

    public void setAutoId(int autoId) {
        this.autoId = autoId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    @Override
    public String toString() {
        return "UserFileDetail{" +
                "autoId=" + autoId +
                ", uuid='" + uuid + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", uploadTime=" + uploadTime +
                '}';
    }
}