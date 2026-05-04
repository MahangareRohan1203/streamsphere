package com.streamsphere.controller;

import com.streamsphere.dto.ResolutionResponse;
import com.streamsphere.dto.VideoMapper;
import com.streamsphere.dto.VideoResponse;
import com.streamsphere.entity.SubscriptionTier;
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
import org.springframework.http.HttpStatus;
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

    /**
     * High-performance video streaming endpoint supporting byte-range requests.
     * 
     * Rationale:
     * 1. Access Enforcement: Trusts 'X-User-Role' injected by API Gateway to perform tier-based checks.
     *    This ensures business-level authorization without re-validating JWTs.
     * 2. Range Requests: Implements HTTP 206 (Partial Content) to support scrubbing, seeking,
     *    and efficient buffering in modern video players.
     * 3. Resolution Switching: Dynamically selects the S3 bucket and file based on requested resolution
     *    (e.g., 720p vs raw).
     * 
     * @param userRole Injected from API Gateway via 'X-User-Role' header.
     * @param rangeHeader Standard HTTP Range header (e.g., 'bytes=0-1024').
     * @param id The video entity ID.
     * @param resolution Optional resolution to stream (e.g., '720p').
     */
    @GetMapping("/{id}/stream")
    public ResponseEntity<StreamingResponseBody> streamVideo(
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "Range", required = false) String rangeHeader,
            @PathVariable Long id,
            @RequestParam(value = "resolution", required = false) String resolution) throws Exception {
        
        Video video = videoService.getVideo(id);
        
        // Access Enforcement: Business-level authorization logic.
        // We compare the numeric 'weight' of the user's tier against the video's requirement.
        SubscriptionTier requiredTier = video.getMinimumSubscriptionTier();
        SubscriptionTier userTier = SubscriptionTier.FREE;
        
        if (userRole != null) {
            try {
                userTier = SubscriptionTier.valueOf(userRole.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Unknown user role: {}. Falling back to FREE.", userRole);
            }
        }

        if (userTier.getWeight() < requiredTier.getWeight()) {
            log.warn("Access denied for video {}. Required: {}, User: {}", id, requiredTier, userTier);
            return ResponseEntity.status(403).build();
        }

        String bucket = "raw-videos";
        String fileName = video.getRawVideoUrl();

        // Resolution switching: Selects the processed version if available and requested.
        if (resolution != null && video.getResolutions() != null) {
            for (VideoResolution res : video.getResolutions()) {
                if (res.getResolution().equalsIgnoreCase(resolution)) {
                    bucket = "processed-videos";
                    fileName = res.getVideoUrl();
                    break;
                }
            }
        }

        long fileSize = videoService.getFileSize(bucket, fileName);
        long start = 0;
        long end = fileSize - 1;

        // Byte-Range Parsing: Standard HTTP/1.1 Range header implementation.
        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String[] ranges = rangeHeader.substring(6).split("-");
            try {
                start = Long.parseLong(ranges[0]);
                if (ranges.length > 1) {
                    end = Long.parseLong(ranges[1]);
                }
            } catch (NumberFormatException e) {
                log.warn("Invalid range header: {}", rangeHeader);
            }
        }

        if (start >= fileSize) {
            return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                    .header(HttpHeaders.CONTENT_RANGE, "bytes */" + fileSize)
                    .build();
        }

        long contentLength = end - start + 1;
        final long finalStart = start;
        final long finalContentLength = contentLength;
        final String finalBucket = bucket;
        final String finalFileName = fileName;

        // Streaming Response: Uses a lambda to stream content directly to the output stream.
        // This is memory efficient as it avoids loading the entire file into memory.
        StreamingResponseBody responseBody = outputStream -> {
            try (InputStream inputStream = videoService.streamVideo(finalBucket, finalFileName, finalStart, finalContentLength)) {
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

        ResponseEntity.BodyBuilder builder = ResponseEntity.status(rangeHeader != null ? HttpStatus.PARTIAL_CONTENT : HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .contentLength(contentLength)
                .contentType(MediaType.parseMediaType("video/mp4"));

        if (rangeHeader != null) {
            builder.header(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileSize);
        }

        return builder.body(responseBody);
    }
}
