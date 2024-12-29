package com.example.gifserverv3.domain.like.repository;

import com.example.gifserverv3.domain.auth.entity.UserEntity;
import com.example.gifserverv3.domain.like.entity.LikeEntity;
import com.example.gifserverv3.domain.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long> {

    // 특정 user와 post에 대한 LikeEntity 조회
    Optional<LikeEntity> findByUserAndPost(UserEntity user, PostEntity post);
}
