package com.example.gifserverv3.domain.like.service;

import com.example.gifserverv3.domain.auth.entity.UserEntity;
import com.example.gifserverv3.domain.auth.repository.UserRepository;
import com.example.gifserverv3.domain.like.repository.LikeRepository;
import com.example.gifserverv3.domain.post.entity.PostEntity;
import com.example.gifserverv3.domain.post.repository.PostRepository;
import com.example.gifserverv3.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.gifserverv3.global.type.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    public boolean checkIfUserLikedPost(Long userId, Long postId) {
        if (userId == null) {
            throw new CustomException(NOT_FOUND_USER);
        }

        return postRepository.isLikedByUser(userId, postId);
    }

    // postId에 해당하는 모든 LikeEntity 삭제
    public void deleteLikesByPostId(Long postId, Long userId) {

        // User 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        // Post 조회
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(NOT_MATCH_POST));

        // 수정 권한 확인
        if (!post.getWriterId().equals(user.getId())) {
            throw new CustomException(INVALID_AUTHORIZED);
        }

        likeRepository.deleteByPostId(postId);
    }
}
