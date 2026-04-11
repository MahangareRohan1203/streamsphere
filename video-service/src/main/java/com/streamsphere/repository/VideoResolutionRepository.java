package com.streamsphere.repository;

import com.streamsphere.entity.VideoResolution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoResolutionRepository extends JpaRepository<VideoResolution, Long> {
}
