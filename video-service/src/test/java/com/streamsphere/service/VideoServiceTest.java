package com.streamsphere.service;

import com.streamsphere.dto.VideoUploadedEvent;
import com.streamsphere.entity.Video;
import com.streamsphere.entity.VideoStatus;
import com.streamsphere.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoServiceTest {

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private StorageService storageService;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private VideoService videoService;

    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() {
        mockFile = new MockMultipartFile(
                "file",
                "test.mp4",
                "video/mp4",
                "test video content".getBytes()
        );
    }

    @Test
    void uploadVideo_Success() throws Exception {
        // Arrange
        String title = "Test Video";
        String description = "Test Description";
        String rawUrl = "raw-videos/unique_test.mp4";

        when(storageService.uploadFile(anyString(), anyString(), eq(mockFile))).thenReturn(rawUrl);
        when(videoRepository.save(any(Video.class))).thenAnswer(invocation -> {
            Video v = invocation.getArgument(0);
            v.setId(1L);
            return v;
        });

        // Act
        Video result = videoService.uploadVideo(title, description, mockFile);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(title, result.getTitle());
        assertEquals(VideoStatus.UPLOADED, result.getStatus());
        assertEquals(rawUrl, result.getRawVideoUrl());

        verify(storageService).uploadFile(eq("raw-videos"), anyString(), eq(mockFile));
        verify(videoRepository).save(any(Video.class));
        verify(kafkaTemplate).send(eq("video-events"), eq("1"), any(VideoUploadedEvent.class));
    }
}
