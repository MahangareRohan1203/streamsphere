package com.streamsphere.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "minimum_subscription_tier")
    @Builder.Default
    private SubscriptionTier minimumSubscriptionTier = SubscriptionTier.FREE;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<VideoResolution> resolutions = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
