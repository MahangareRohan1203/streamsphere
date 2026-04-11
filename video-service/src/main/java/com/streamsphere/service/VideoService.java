package com.streamsphere.service;

import com.streamsphere.dto.VideoUploadedEvent;
import com.streamsphere.entity.Video;
import com.streamsphere.entity.VideoStatus;
import com.streamsphere.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoService {

    private final VideoRepository videoRepository;
    private final StorageService storageService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String RAW_BUCKET = "raw-videos";
    private static final String TOPIC = "video-events";

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
