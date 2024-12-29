package com.example.gifserverv3.domain.like.repository;

import com.example.gifserverv3.domain.auth.entity.UserEntity;
import com.example.gifserverv3.domain.like.entity.LikeEntity;
import com.example.gifserverv3.domain.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long> {

    Optional<LikeEntity> findByUserAndPost(UserEntity user, PostEntity post);

    @Modifying
    @Transactional
    @Query("DELETE FROM LikeEntity l WHERE l.post.postid = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}
