package com.streamsphere.controller;

import com.streamsphere.entity.Video;
import com.streamsphere.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
@Slf4j
public class VideoController {

    private final VideoService videoService;

    @PostMapping("/upload")
    public ResponseEntity<Video> uploadVideo(
            @RequestHeader(value = "X-User-Name", required = false) String username,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("file") MultipartFile file) throws Exception {
        
        log.info("Received upload request. User: {}, Role: {}, Title: {}", username, role, title);

        // Example of role-based check in the controller
        if (!"ADMIN".equalsIgnoreCase(role)) {
            log.warn("Access denied for user {} with role {}. ADMIN role required.", username, role);
            return ResponseEntity.status(403).build();
        }

        Video video = videoService.uploadVideo(title, description, file);
        return ResponseEntity.ok(video);
    }
}
