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

    // 특정 user가 좋아요한 모든 post 조회
    List<LikeEntity> findByUser(UserEntity user);

    // 특정 post에 대한 좋아요 상태를 조회 (리스트 반환)
    List<LikeEntity> findByPost(PostEntity post);

    // 좋아요 상태가 true인 경우만 조회 (예: 좋아요를 누른 게시글만)
    List<LikeEntity> findByPostAndLikedTrue(PostEntity post);
}
