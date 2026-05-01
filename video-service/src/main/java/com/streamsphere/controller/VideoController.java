package com.streamsphere.controller;

import com.streamsphere.dto.ResolutionResponse;
import com.streamsphere.dto.VideoMapper;
import com.streamsphere.dto.VideoResponse;
import com.streamsphere.entity.Video;
import com.streamsphere.entity.VideoResolution;
import com.streamsphere.entity.VideoStatus;
import com.streamsphere.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
@Slf4j
public class VideoController {

    private final VideoService videoService;
    private final VideoMapper videoMapper;

    @GetMapping
    public ResponseEntity<Page<VideoResponse>> getAllVideos(
            @RequestParam(value = "status", required = false) VideoStatus status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        
        Page<Video> videoPage = videoService.getAllVideos(status, pageable);
        return ResponseEntity.ok(videoPage.map(videoMapper::toResponse));
    }

    @PostMapping("/upload")
    public ResponseEntity<VideoResponse> uploadVideo(
            @RequestHeader(value = "X-User-Name", required = false) String username,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("file") MultipartFile file) throws Exception {
        
        log.info("Received upload request. User: {}, Role: {}, Title: {}", username, role, title);

        // Strict role check: Only ADMIN can upload
        if (!"ADMIN".equalsIgnoreCase(role)) {
            log.warn("Access denied for user {} with role {}. ADMIN role required.", username, role);
            return ResponseEntity.status(403).build();
        }

        Video video = videoService.uploadVideo(title, description, file);
        return ResponseEntity.ok(videoMapper.toResponse(video));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoResponse> getVideo(@PathVariable Long id) {
        return ResponseEntity.ok(videoMapper.toResponse(videoService.getVideo(id)));
    }

    @GetMapping("/{id}/stream")
    public ResponseEntity<StreamingResponseBody> streamVideo(
            @PathVariable Long id,
            @RequestParam(value = "resolution", required = false) String resolution) {
        
        Video video = videoService.getVideo(id);
        String bucket = "raw-videos";
        String fileName = video.getRawVideoUrl();

        if (resolution != null && video.getResolutions() != null) {
            for (VideoResolution res : video.getResolutions()) {
                if (res.getResolution().equalsIgnoreCase(resolution)) {
                    bucket = "processed-videos";
                    fileName = res.getVideoUrl();
                    break;
                }
            }
        }

        final String finalBucket = bucket;
        final String finalFileName = fileName;

        StreamingResponseBody responseBody = outputStream -> {
            try (InputStream inputStream = videoService.streamVideo(finalBucket, finalFileName)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            } catch (Exception e) {
                log.error("Error streaming video: {}", e.getMessage());
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(responseBody);
    }
}
