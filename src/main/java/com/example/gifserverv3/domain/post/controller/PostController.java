package com.example.gifserverv3.domain.post.controller;

import com.example.gifserverv3.domain.like.service.LikeService;
import com.example.gifserverv3.domain.post.dto.request.CreateRequest;
import com.example.gifserverv3.domain.post.dto.request.UpdateRequest;
import com.example.gifserverv3.domain.post.dto.response.AllPostResponse;
import com.example.gifserverv3.domain.post.dto.response.CreateResponse;
import com.example.gifserverv3.domain.post.dto.response.SinglePostResponse;
import com.example.gifserverv3.domain.post.entity.PostEntity;
import com.example.gifserverv3.domain.post.service.PostService;
import com.example.gifserverv3.global.login.LoginCheck;
import com.example.gifserverv3.global.util.MsgResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Controller
@RestController
@RequestMapping("api/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private LikeService likeService;

    @PostMapping("/create")
    @LoginCheck
    public ResponseEntity<Object> createPost(
            @RequestBody @Valid CreateRequest createRequest,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("user");

        // 서비스에 사용자 정보와 함께 게시물 생성 요청
        PostEntity createPost = postService.createPost(userId, createRequest);

        // 성공 메시지 반환
        CreateResponse responseDto = new CreateResponse("글 작성이 성공적으로 완료되었습니다.", 200, createPost.getPostid());

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{id}")
    @LoginCheck
    public ResponseEntity<Object> getPostById(@PathVariable Long id, HttpServletRequest session) {

        // 세션에서 사용자 정보 가져오기
        Long userId = (Long) session.getAttribute("user");

        PostEntity post = postService.getPostById(id, userId); // 서비스에서 특정 id로 게시물 조회

        // PostEntity를 SinglePostResponse로 변환하여 응답 반환
        SinglePostResponse responseDto = convertToSinglePostResponse(post);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/user")
    @LoginCheck
    public List<PostEntity> getPostsBySession(HttpSession session) {
        Long userId = (Long) session.getAttribute("user");

        return postService.getPostsBySession(userId);
    }

    // 모든 게시물을 조회하는 엔드포인트
    @GetMapping
    @LoginCheck
    public ResponseEntity<Object> getAllPosts(HttpSession session) {

        // 세션에서 사용자 정보 가져오기
        Long userId = (Long) session.getAttribute("user");

        List<PostEntity> posts = postService.getAllPosts(userId);

        // PostEntity 리스트를 AllPostResponse로 변환하여 응답 반환
        List<SinglePostResponse> postResponses = posts.stream()
                .map(this::convertToSinglePostResponse)
                .collect(Collectors.toList());

        AllPostResponse responseDto = new AllPostResponse(postResponses);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{postId}")
    @LoginCheck
    public ResponseEntity<MsgResponseDto> updatePost(@PathVariable Long postId, @RequestBody @Valid UpdateRequest requestDto, HttpSession session) {

        // 세션에서 사용자 정보 가져오기
        Long userId = (Long) session.getAttribute("user");

        postService.updatePost(postId, requestDto, userId);

        MsgResponseDto responseDto = new MsgResponseDto("게시물 수정이 성공적으로 완료되었습니다.", 200);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{postId}/update")
    @LoginCheck
    public ResponseEntity<MsgResponseDto> updatePost(@PathVariable Long postId, HttpSession session) {
        // 세션에서 사용자 정보 가져오기
        Long userId = (Long) session.getAttribute("user");

        postService.updateTime(postId, userId);

        MsgResponseDto responseDto = new MsgResponseDto("게시물 시간 업데이트가 성공적으로 완료되었습니다.", 200);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{postId}/delete")
    @LoginCheck
    public ResponseEntity<MsgResponseDto> deletePost(@PathVariable Long postId, HttpSession session) {
        // 세션에서 사용자 정보 가져오기
        Long userId = (Long) session.getAttribute("user");

        likeService.deleteLikesByPostId(postId, userId);

        postService.deletePost(postId, userId);

        MsgResponseDto responseDto = new MsgResponseDto("게시물이 성공적으로 삭제되었습니다.", 200);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/{postId}/like")
    @LoginCheck
    public ResponseEntity<MsgResponseDto> toggleLike(@PathVariable Long postId, HttpSession session) {
        // 세션에서 user 가져오기
        Long userId = (Long) session.getAttribute("user");

        // 좋아요 처리
        boolean isLiked = postService.toggleLike(userId, postId);

        // 상태에 따른 메시지 생성
        String message = isLiked ? "좋아요를 눌렀습니다." : "좋아요를 취소하였습니다.";
        MsgResponseDto responseDto = new MsgResponseDto(message, HttpStatus.OK.value());

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{postId}/like-status")
    @LoginCheck
    public ResponseEntity<Map<String, Boolean>> checkLikeStatus(HttpSession session, @PathVariable Long postId) {

        Long userId = (Long) session.getAttribute("user");

        boolean isLiked = likeService.checkIfUserLikedPost(userId, postId);

        Map<String, Boolean> response = new HashMap<>();

        response.put("liketrueorfalse", isLiked);

        return ResponseEntity.ok(response);
    }

    // PostEntity를 SinglePostResponse로 변환하는 메소드
    private SinglePostResponse convertToSinglePostResponse(PostEntity postEntity) {

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

}