package com.example.gifserverv3.domain.post.service;

import com.example.gifserverv3.domain.auth.dto.UserDto;
import com.example.gifserverv3.domain.post.dto.request.CreateRequest;
import com.example.gifserverv3.domain.post.dto.request.UpdateRequest;
import com.example.gifserverv3.domain.post.dto.response.AllPostResponse;
import com.example.gifserverv3.domain.post.dto.response.SinglePostResponse;
import com.example.gifserverv3.domain.post.entity.PostEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PostService {

    /**
     * 게시물 생성
     */
    PostEntity createPost(Long userId, CreateRequest createRequest);

    /**
     * 게시물 찾기(ID)
     */
    PostEntity getPostById(Long id, Long userId);

    /**
     * 모든 게시물 찾기
     */
    List<PostEntity> getAllPosts(Long userId);

    /**
     * 게시물 업데이트
     */
    void updatePost(Long postId, UpdateRequest requestDto, Long userId);

    /**
     * SinglePostResponse로 변환
     */
    SinglePostResponse convertToSinglePostResponse(PostEntity postEntity);

    /**
     * AllPostResponse로 변한
     */
    AllPostResponse convertToAllPostResponse(List<PostEntity> postEntities);

    /**
     * 시간 업데이트
     */
    void updateTime(Long postId, Long userId);

    /**
     * 게시물 삭제
     */
    void deletePost(Long postId, Long userId);

    /**
     * 게시물 좋아요
     */
    boolean toggleLike(Long userId, Long postId);
}

