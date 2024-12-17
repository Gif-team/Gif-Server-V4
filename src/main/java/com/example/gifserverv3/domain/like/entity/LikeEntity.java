package com.example.gifserverv3.domain.like.entity;

import com.example.gifserverv3.domain.auth.entity.UserEntity;
import com.example.gifserverv3.domain.post.entity.PostEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "likes")
public class LikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 좋아요 ID

    @ManyToOne
    @JoinColumn(name = "userId")
    private UserEntity user; // 좋아요를 누른 사용자

    @ManyToOne
    @JoinColumn(name = "postId")
    private PostEntity post; // 좋아요를 받은 게시글

    private boolean liked; // 좋아요 여부 (true: 좋아요, false: 안누름)

    // 빌더 패턴을 사용한 LikeEntity 클래스
    @Builder
    public LikeEntity(UserEntity user, PostEntity post, boolean liked) {
        this.user = user;
        this.post = post;
        this.liked = liked;
    }

    // 비즈니스 메서드 - 좋아요 상태를 변경
    public void toggleLike() {
        this.liked = !this.liked; // 좋아요 상태를 반전시킴
    }
}

