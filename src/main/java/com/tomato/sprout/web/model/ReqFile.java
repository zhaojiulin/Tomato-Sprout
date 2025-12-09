package com.tomato.sprout.web.model;

import java.io.InputStream;

/**
 * @author zhaojiulin
 * @version 1.0
 * @description: 请求文件
 *  fileInfo.put("fileName", fileName);
 *                             fileInfo.put("fileSize", part.getSize());
 *                             fileInfo.put("contentType", part.getContentType());
 *                             fileInfo.put("fileBytes", fileBytes);
 *                             fileInfo.put("originalPart", part); // 可以存储原始Part对象，用于后续处理
 * @date 2025/12/9 9:59
 */
public class ReqFile {
    private String fileName;
    private Long fileSize;
    private String contentType;
    private byte[] fileBytes;
    private InputStream inputStream;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public void setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
