package com.streamsphere.service;

import com.streamsphere.dto.VideoUploadedEvent;
import com.streamsphere.entity.Video;
import com.streamsphere.entity.VideoResolution;
import com.streamsphere.entity.VideoStatus;
import com.streamsphere.repository.VideoRepository;
import com.streamsphere.repository.VideoResolutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoTranscodingListenerTest {

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private VideoResolutionRepository videoResolutionRepository;

    @Mock
    private StorageService storageService;

    @Mock
    private TranscodingService transcodingService;

    @InjectMocks
    private VideoTranscodingListener listener;

    private Video video;
    private VideoUploadedEvent event;

    @BeforeEach
    void setUp() {
        video = Video.builder()
                .id(1L)
                .title("Test Video")
                .originalFileName("test.mp4")
                .rawVideoUrl("raw_test.mp4")
                .status(VideoStatus.UPLOADED)
                .build();

        event = VideoUploadedEvent.builder()
                .videoId(1L)
                .rawVideoUrl("raw_test.mp4")
                .build();
    }

    @Test
    void handleVideoUploaded_Success() throws Exception {
        // Arrange
        when(videoRepository.findById(1L)).thenReturn(Optional.of(video));
        when(storageService.downloadFile(anyString(), anyString())).thenReturn(mock(InputStream.class));
        when(transcodingService.transcode(any(File.class), anyString())).thenReturn(File.createTempFile("transcoded", ".mp4"));
        when(storageService.uploadFile(anyString(), anyString(), any(File.class))).thenReturn("processed_url");

        // Act
        listener.handleVideoUploaded(event);

        // Assert
        assertEquals(VideoStatus.COMPLETED, video.getStatus());
        assertEquals(3, video.getResolutions().size());
        verify(videoResolutionRepository, times(3)).save(any(VideoResolution.class));
        verify(videoRepository, times(2)).save(video);
    }

    @Test
    void handleVideoUploaded_Failure() throws Exception {
        // Arrange
        when(videoRepository.findById(1L)).thenReturn(Optional.of(video));
        when(storageService.downloadFile(anyString(), anyString())).thenThrow(new RuntimeException("Download failed"));

        // Act
        listener.handleVideoUploaded(event);

        // Assert
        assertEquals(VideoStatus.FAILED, video.getStatus());
        verify(videoRepository, times(2)).save(video);
    }
}
