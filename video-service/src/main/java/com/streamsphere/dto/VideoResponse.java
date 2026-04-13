package com.streamsphere.dto;

import com.streamsphere.entity.VideoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoResponse {
    private Long id;
    private String title;
    private String description;
    private String originalFileName;
    private String rawVideoUrl;
    private VideoStatus status;
    private LocalDateTime createdAt;
    private List<ResolutionResponse> resolutions;
}
