package com.example.gifserverv3.domain.post.repository;

import com.example.gifserverv3.domain.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
    @Query("SELECT p FROM PostEntity p WHERE p.writerId = :userId ORDER BY p.realtime DESC")
    List<PostEntity> getPostsBySession(@Param("userId") Long userId);

    @Query("SELECT COUNT(p) FROM PostEntity p WHERE p.writerId = :userId AND p.category = true")
    long countTrueCategoryPostsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(p) FROM PostEntity p WHERE p.writerId = :userId AND p.category = false")
    long countFalseCategoryPostsByUserId(@Param("userId") Long userId);

    @Query("SELECT MAX(p.likeNumber) FROM PostEntity p WHERE p.writerId = :userId")
    Integer findMaxLikeNumberByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(l.liked, false) FROM LikeEntity l WHERE l.user.id = :userId AND l.post.postid = :postId")
    boolean isLikedByUser(@Param("userId") Long userId, @Param("postId") Long postId);
}
