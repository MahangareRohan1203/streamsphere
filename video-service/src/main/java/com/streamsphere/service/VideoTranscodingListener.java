package com.streamsphere.service;

import com.streamsphere.dto.VideoUploadedEvent;
import com.streamsphere.entity.Video;
import com.streamsphere.entity.VideoResolution;
import com.streamsphere.entity.VideoStatus;
import com.streamsphere.repository.VideoRepository;
import com.streamsphere.repository.VideoResolutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class VideoTranscodingListener {

    private final VideoRepository videoRepository;
    private final VideoResolutionRepository videoResolutionRepository;
    private final StorageService storageService;
    private final TranscodingService transcodingService;

    private static final String RAW_BUCKET = "raw-videos";
    private static final String PROCESSED_BUCKET = "processed-videos";

    @KafkaListener(topics = "video-events", groupId = "video-processing-group")
    public void handleVideoUploaded(VideoUploadedEvent event) {
        log.info("Received video uploaded event for video id: {}", event.getVideoId());

        Video video = videoRepository.findById(event.getVideoId()).orElseThrow();
        video.setStatus(VideoStatus.PROCESSING);
        videoRepository.save(video);

        File tempFile = null;
        try {
            // Download to temp file
            tempFile = File.createTempFile("raw_", "_" + video.getOriginalFileName());
            try (InputStream is = storageService.downloadFile(RAW_BUCKET, event.getRawVideoUrl())) {
                Files.copy(is, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            String[] resolutions = {"1080p", "720p", "480p"};
            video.setResolutions(new ArrayList<>());

            for (String resolution : resolutions) {
                log.info("Transcoding video {} to {}", video.getId(), resolution);
                File transcodedFile = transcodingService.transcode(tempFile, resolution);

                String objectName = resolution + "_" + video.getOriginalFileName();
                String videoUrl = storageService.uploadFile(PROCESSED_BUCKET, objectName, transcodedFile);

                VideoResolution res = VideoResolution.builder()
                        .video(video)
                        .resolution(resolution)
                        .videoUrl(videoUrl)
                        .build();

                video.getResolutions().add(res);
                videoResolutionRepository.save(res);
                transcodedFile.delete();
            }

            video.setStatus(VideoStatus.COMPLETED);
            videoRepository.save(video);
            log.info("Successfully transcoded video id: {}", video.getId());

        } catch (Exception e) {
            log.error("Error processing video id: {}", event.getVideoId(), e);
            video.setStatus(VideoStatus.FAILED);
            videoRepository.save(video);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}
