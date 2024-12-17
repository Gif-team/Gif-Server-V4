package com.example.gifserverv3.domain.badge.repository;

import com.example.gifserverv3.domain.badge.entity.BadgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface BadgeRepository extends JpaRepository<BadgeEntity, Long> {

    // userId로 BadgeEntity 조회
    Optional<BadgeEntity> findByUserId(Long userId);
}

