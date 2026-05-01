package com.streamsphere.service;

import com.streamsphere.entity.Video;
import com.streamsphere.entity.VideoStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.List;

public interface VideoService {
    Video getVideo(Long id);
    Page<Video> getAllVideos(VideoStatus status, Pageable pageable);
    InputStream streamVideo(String bucket, String fileName) throws Exception;
    Video uploadVideo(String title, String description, MultipartFile file) throws Exception;
}
