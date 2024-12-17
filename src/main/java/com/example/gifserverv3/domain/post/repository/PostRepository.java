package com.example.gifserverv3.domain.post.repository;

import com.example.gifserverv3.domain.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
    @Query("SELECT COUNT(p) FROM PostEntity p WHERE p.writerId = :userId AND p.category = true")
    long countTrueCategoryPostsByUserId(@Param("userId") Long userId);

}
