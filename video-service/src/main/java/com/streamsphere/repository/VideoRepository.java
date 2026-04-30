package com.streamsphere.repository;

import com.streamsphere.entity.Video;
import com.streamsphere.entity.VideoStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    Page<Video> findByStatus(VideoStatus status, Pageable pageable);
}
