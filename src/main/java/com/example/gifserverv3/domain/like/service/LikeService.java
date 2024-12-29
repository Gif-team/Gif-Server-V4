package com.example.gifserverv3.domain.like.service;

import org.springframework.stereotype.Service;


@Service
public interface LikeService {

    /**
     * 특정 게시물 특정 사용자 좋아요 여부
     */
    boolean checkIfUserLikedPost(Long userId, Long postId);
}
