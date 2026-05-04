package com.streamsphere.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.InputStream;

public interface StorageService {
    void createBucketIfNotExists(String bucketName);
    String uploadFile(String bucketName, String objectName, MultipartFile file) throws Exception;
    String uploadFile(String bucketName, String objectName, File file) throws Exception;
    InputStream downloadFile(String bucketName, String objectName) throws Exception;
    InputStream downloadFile(String bucketName, String objectName, long offset, long length) throws Exception;
    long getFileSize(String bucketName, String objectName) throws Exception;
}
