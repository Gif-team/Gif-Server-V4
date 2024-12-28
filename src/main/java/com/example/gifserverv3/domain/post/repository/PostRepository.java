package com.example.gifserverv3.domain.post.repository;

import com.example.gifserverv3.domain.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
    @Query("SELECT COUNT(p) FROM PostEntity p WHERE p.writerId = :userId AND p.category = true")
    long countTrueCategoryPostsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(p) FROM PostEntity p WHERE p.writerId = :userId AND p.category = false")
    long countFalseCategoryPostsByUserId(@Param("userId") Long userId);

    @Query("SELECT MAX(p.likeNumber) FROM PostEntity p WHERE p.writerId = :userId")
    Integer findMaxLikeNumberByUserId(@Param("userId") Long userId);

    // 좋아요 수 증가
    @Modifying
    @Query("UPDATE PostEntity p SET p.likeNumber = p.likeNumber + 1 WHERE p.postid = :postId")
    void incrementLikeNumber(@Param("postId") Long postId);

    // 좋아요 수 감소
    @Modifying
    @Query("UPDATE PostEntity p SET p.likeNumber = p.likeNumber - 1 WHERE p.postid = :postId")
    void decrementLikeNumber(@Param("postId") Long postId);
}
