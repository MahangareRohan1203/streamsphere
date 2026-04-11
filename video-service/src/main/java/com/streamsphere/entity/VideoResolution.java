package com.streamsphere.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "video_resolutions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoResolution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Column(nullable = false)
    private String resolution; // e.g., 1080p, 720p, 480p

    @Column(nullable = false)
    private String videoUrl;
}
