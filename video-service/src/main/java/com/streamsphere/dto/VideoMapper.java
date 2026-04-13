package com.streamsphere.dto;

import com.streamsphere.entity.Video;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class VideoMapper {

    public VideoResponse toResponse(Video video) {
        if (video == null) {
            return null;
        }

        List<ResolutionResponse> resolutions = video.getResolutions() == null ? Collections.emptyList() :
                video.getResolutions().stream()
                        .map(res -> ResolutionResponse.builder()
                                .resolution(res.getResolution())
                                .videoUrl(res.getVideoUrl())
                                .build())
                        .collect(Collectors.toList());

        return VideoResponse.builder()
                .id(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .originalFileName(video.getOriginalFileName())
                .rawVideoUrl(video.getRawVideoUrl())
                .status(video.getStatus())
                .createdAt(video.getCreatedAt())
                .resolutions(resolutions)
                .build();
    }
}
