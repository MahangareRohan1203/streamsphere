package com.streamsphere.service;

import com.streamsphere.entity.Video;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

public interface VideoService {
    Video getVideo(Long id);
    InputStream streamVideo(String bucket, String fileName) throws Exception;
    Video uploadVideo(String title, String description, MultipartFile file) throws Exception;
}
