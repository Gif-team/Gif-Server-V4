package com.example.gifserverv3.domain.image.repository;

import com.example.gifserverv3.domain.image.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
    List<ImageEntity> findByPostId(Long postId);
}
