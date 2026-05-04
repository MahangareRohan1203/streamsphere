package com.streamsphere.service;

import com.streamsphere.dto.VideoUploadedEvent;
import com.streamsphere.entity.Video;
import com.streamsphere.entity.VideoStatus;
import com.streamsphere.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;
    private final StorageService storageService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String RAW_BUCKET = "raw-videos";
    private static final String PROCESSED_BUCKET = "processed-videos";
    private static final String TOPIC = "video-events";

    @Override
    public Video getVideo(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video not found"));
    }

    @Override
    public Page<Video> getAllVideos(VideoStatus status, Pageable pageable) {
        if (status != null) {
            return videoRepository.findByStatus(status, pageable);
        }
        return videoRepository.findAll(pageable);
    }

    @Override
    public InputStream streamVideo(String bucket, String fileName) throws Exception {
        return storageService.downloadFile(bucket, fileName);
    }

    /**
     * Retrieves a specific byte range from the storage service.
     * 
     * Rationale: Efficiently supports HTTP 206 (Partial Content) by only fetching the requested
     * bytes from S3/MinIO. This reduces bandwidth and improves latency for seeking operations
     * in the video player.
     * 
     * @param bucket The S3 bucket name.
     * @param fileName The object key in the bucket.
     * @param offset The starting byte position.
     * @param length The number of bytes to retrieve.
     * @return InputStream containing the requested byte range.
     */
    @Override
    public InputStream streamVideo(String bucket, String fileName, long offset, long length) throws Exception {
        return storageService.downloadFile(bucket, fileName, offset, length);
    }

    @Override
    public long getFileSize(String bucket, String fileName) throws Exception {
        return storageService.getFileSize(bucket, fileName);
    }

    @Override
    public Video uploadVideo(String title, String description, MultipartFile file) throws Exception {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String rawVideoUrl = storageService.uploadFile(RAW_BUCKET, fileName, file);

        Video video = Video.builder()
                .title(title)
                .description(description)
                .originalFileName(file.getOriginalFilename())
                .rawVideoUrl(rawVideoUrl)
                .status(VideoStatus.UPLOADED)
                .build();

        video = videoRepository.save(video);

        VideoUploadedEvent event = VideoUploadedEvent.builder()
                .videoId(video.getId())
                .rawVideoUrl(rawVideoUrl)
                .build();

        kafkaTemplate.send(TOPIC, String.valueOf(video.getId()), event);
        log.info("Published video uploaded event for video id: {}", video.getId());

        return video;
    }
}
