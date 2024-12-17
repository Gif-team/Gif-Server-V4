package com.example.gifserverv3.domain.post.service;

import com.example.gifserverv3.domain.auth.entity.UserEntity;
import com.example.gifserverv3.domain.auth.repository.UserRepository;
import com.example.gifserverv3.domain.like.entity.LikeEntity;
import com.example.gifserverv3.domain.like.repository.LikeRepository;
import com.example.gifserverv3.domain.post.dto.request.CreateRequest;
import com.example.gifserverv3.domain.post.dto.request.UpdateRequest;
import com.example.gifserverv3.domain.post.dto.response.AllPostResponse;
import com.example.gifserverv3.domain.post.dto.response.SinglePostResponse;
import com.example.gifserverv3.domain.post.entity.PostEntity;
import com.example.gifserverv3.domain.post.repository.PostRepository;
import com.example.gifserverv3.global.exception.CustomException;
import com.example.gifserverv3.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.gifserverv3.global.exception.ErrorCode.*;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpSession httpSession;
    @Autowired
    private LikeRepository likeRepository;

    public PostEntity createPost(CreateRequest createRequest, HttpServletRequest request) {
        // 세션에서 사용자 정보 찾기
        UserEntity user = (UserEntity) request.getSession().getAttribute("user");

        // 사용자 정보가 없으면 예외 처리 (필요한 경우)
        if (user == null) {
            throw new CustomException(NOT_MATCH_INFORMATION);
        }

        // 게시물 생성
        LocalDateTime now = LocalDateTime.now();
        PostEntity postEntity = PostEntity.builder()
                .title(createRequest.getTitle())
                .content(createRequest.getContent())
                .category(createRequest.isCategory())
                .price(createRequest.getPrice())
                .realtime(now)
                .writer(user.getUsername())
                .writerId(user.getUserId())
                .building(new PostEntity.Building(createRequest.getBuilding().getId(), createRequest.getBuilding().getFloor()))
                .build();

        // 저장 후 반환
        return postRepository.save(postEntity);
    }
    // 특정 게시물 조회
    public PostEntity getPostById(Long id) {
        // PostRepository에서 특정 ID에 해당하는 게시물을 찾음
        Optional<PostEntity> post = postRepository.findById(id);
        if (post.isPresent()) {
            return post.get();
        } else {
            // 예외 처리 (존재하지 않는 게시물일 경우)
            throw new RuntimeException("Post not found with id: " + id);
        }
    }

    // 모든 게시물 조회
    public List<PostEntity> getAllPosts() {
        // 모든 게시물 조회
        return postRepository.findAll();
    }









}
