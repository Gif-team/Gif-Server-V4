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

    // SinglePostResponse로 변환
    public SinglePostResponse convertToSinglePostResponse(PostEntity postEntity) {
        SinglePostResponse.BuildingResponseDto buildingDto = new SinglePostResponse.BuildingResponseDto(
                postEntity.getBuilding().getId(), postEntity.getBuilding().getFloor());

        return new SinglePostResponse(
                postEntity.getPostid(),
                postEntity.getTitle(),
                postEntity.getContent(),
                postEntity.isCategory(),
                postEntity.getPrice(),
                postEntity.getRealtime(),
                postEntity.getWriter(),
                postEntity.getWriterId(),
                postEntity.getLikeNumber(),
                buildingDto
        );
    }

    // AllPostResponse로 변환
    public AllPostResponse convertToAllPostResponse(List<PostEntity> postEntities) {
        List<SinglePostResponse> postResponses = postEntities.stream()
                .map(this::convertToSinglePostResponse)
                .collect(Collectors.toList());

        return new AllPostResponse(postResponses);
    }

    @Transactional
    public void updatePost(Long postId, UpdateRequest requestDto, UserEntity user) {
        // Post 조회
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(NOT_MATCH_POST));

        // 수정 권한 확인
        if (!post.getWriterId().equals(user.getUserId())) {
            throw new CustomException(INVALID_AUTHORIZED);
        }

        // 수정 가능한 필드 업데이트
        post.setTitle(requestDto.getTitle());
        post.setContent(requestDto.getContent());
        post.setCategory(requestDto.isCategory());
        post.setPrice(requestDto.getPrice());

        // Building 정보 업데이트 (BuildingRequest로부터 id와 floor 값을 가져옴)
        if (requestDto.getBuilding() != null) {
            post.setBuilding(new PostEntity.Building(requestDto.getBuilding().getId(), requestDto.getBuilding().getFloor()));
        }

        postRepository.save(post);
    }

    @Transactional
    public void updateTime(Long postId, UserEntity user) {
        // Post 조회
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(NOT_MATCH_POST));

        // 수정 권한 확인
        if (!post.getWriterId().equals(user.getUserId())) {
            throw new CustomException(INVALID_AUTHORIZED);
        }

        // 현재 시간으로 'realtime' 필드 업데이트
        post.setRealtime(LocalDateTime.now());

        postRepository.save(post);
    }


    @Transactional
    public void deletePost(Long postId, UserEntity user) {
        // Post 조회
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(NOT_MATCH_POST));

        // 수정 권한 확인
        if (!post.getWriterId().equals(user.getUserId())) {
            throw new CustomException(INVALID_AUTHORIZED);
        }

        // 게시물 삭제
        postRepository.delete(post);
    }

    @Transactional
    public boolean toggleLike(UserEntity user, Long postId) {
        // postId로 PostEntity 조회
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(NOT_MATCH_POST));

        // userId와 postId로 기존 LikeEntity 조회
        Optional<LikeEntity> existingLike = likeRepository.findByUserAndPost(user, post);

        // 기존 좋아요가 없는 경우 새로 생성
        LikeEntity likeEntity = existingLike.orElse(LikeEntity.builder()
                .user(user) // user 정보 설정
                .post(post) // post 정보 설정
                .liked(false) // 초기 상태 false로 설정
                .build());

        // 좋아요 상태 반전
        likeEntity.toggleLike();

        // LikeEntity 저장
        likeRepository.save(likeEntity);

        // 좋아요 수 업데이트
        if (likeEntity.isLiked()) {
            post.setLikeNumber(post.getLikeNumber() + 1);
        } else {
            post.setLikeNumber(post.getLikeNumber() - 1);
        }

        // 게시글 엔티티 저장
        postRepository.save(post);

        // 변경된 좋아요 상태 반환
        return likeEntity.isLiked();
    }






}
