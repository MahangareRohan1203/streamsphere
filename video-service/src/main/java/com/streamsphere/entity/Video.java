package com.streamsphere.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "videos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String rawVideoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VideoStatus status;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VideoResolution> resolutions;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
