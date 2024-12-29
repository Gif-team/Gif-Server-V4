package com.example.gifserverv3.domain.like.service;

import com.example.gifserverv3.domain.post.repository.PostRepository;
import com.example.gifserverv3.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.gifserverv3.global.type.ErrorCode.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    @Autowired
    private PostRepository postRepository;

    public boolean checkIfUserLikedPost(Long userId, Long postId) {
        if (userId == null) {
            throw new CustomException(NOT_FOUND_USER);
        }

        return postRepository.isLikedByUser(userId, postId);
    }
}
