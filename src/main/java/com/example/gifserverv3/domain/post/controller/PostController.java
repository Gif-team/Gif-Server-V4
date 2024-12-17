package com.example.gifserverv3.domain.post.controller;

import com.example.gifserverv3.domain.auth.entity.UserEntity;
import com.example.gifserverv3.domain.post.dto.request.CreateRequest;
import com.example.gifserverv3.domain.post.dto.request.UpdateRequest;
import com.example.gifserverv3.domain.post.dto.response.AllPostResponse;
import com.example.gifserverv3.domain.post.dto.response.SinglePostResponse;
import com.example.gifserverv3.domain.post.entity.PostEntity;
import com.example.gifserverv3.domain.post.service.PostService;
import com.example.gifserverv3.global.util.MsgResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

// Controller
@RestController
@RequestMapping("api/post")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("/create")
    public ResponseEntity<MsgResponseDto> createPost(
            @RequestBody @Valid CreateRequest createRequest,
            HttpServletRequest request) {
        // 세션에서 사용자 정보 찾기
        UserEntity user = (UserEntity) request.getSession().getAttribute("user");

        // 사용자 정보가 없으면 로그인 해주세요 메시지 반환
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MsgResponseDto("로그인 해주세요.", HttpStatus.UNAUTHORIZED.value()));
        }

        // 서비스에 사용자 정보와 함께 게시물 생성 요청
        postService.createPost(createRequest, request);

        // 성공 메시지 반환
        MsgResponseDto responseDto = new MsgResponseDto("글 작성이 성공적으로 완료되었습니다.", 200);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id, HttpServletRequest request) {

        // 세션에서 사용자 정보 가져오기
        HttpSession session = request.getSession(false);
        // 세션이 존재하면 가져오고, 없으면 null 반환
        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MsgResponseDto("로그인 해주세요.", HttpStatus.UNAUTHORIZED.value()));
        }

        PostEntity post = postService.getPostById(id); // 서비스에서 특정 id로 게시물 조회

        // PostEntity를 SinglePostResponse로 변환하여 응답 반환
        SinglePostResponse responseDto = convertToSinglePostResponse(post);
        return ResponseEntity.ok(responseDto);
    }
    // 모든 게시물을 조회하는 엔드포인트
    @GetMapping
    public ResponseEntity<?> getAllPosts(HttpServletRequest request) {
        List<PostEntity> posts = postService.getAllPosts(); // 서비스에서 모든 게시물 조회

        // 세션에서 사용자 정보 가져오기
        HttpSession session = request.getSession(false);
        // 세션이 존재하면 가져오고, 없으면 null 반환
        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MsgResponseDto("로그인 해주세요.", HttpStatus.UNAUTHORIZED.value()));
        }

        // PostEntity 리스트를 AllPostResponse로 변환하여 응답 반환
        List<SinglePostResponse> postResponses = posts.stream()
                .map(this::convertToSinglePostResponse)
                .collect(Collectors.toList());

        AllPostResponse responseDto = new AllPostResponse(postResponses);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<MsgResponseDto> updatePost( @PathVariable Long postId, @RequestBody @Valid UpdateRequest requestDto, HttpSession session) {

        // 세션에서 사용자 정보 가져오기
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MsgResponseDto("로그인 해주세요.", HttpStatus.UNAUTHORIZED.value()));
        }

        postService.updatePost(postId, requestDto, user);

        MsgResponseDto responseDto = new MsgResponseDto("게시물 수정이 성공적으로 완료되었습니다.", 200);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{postId}/update")
    public ResponseEntity<MsgResponseDto> updatePost(@PathVariable Long postId, HttpSession session) {
        // 세션에서 사용자 정보 가져오기
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MsgResponseDto("로그인 해주세요.", HttpStatus.UNAUTHORIZED.value()));
        }

        postService.updateTime(postId, user);

        MsgResponseDto responseDto = new MsgResponseDto("게시물 시간 업데이트가 성공적으로 완료되었습니다.", 200);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{postId}/delete")
    public ResponseEntity<MsgResponseDto> deletePost(@PathVariable Long postId, HttpSession session) {
        // 세션에서 사용자 정보 가져오기
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MsgResponseDto("로그인 해주세요.", HttpStatus.UNAUTHORIZED.value()));
        }

        postService.deletePost(postId, user);

        MsgResponseDto responseDto = new MsgResponseDto("게시물이 성공적으로 삭제되었습니다.", 200);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<MsgResponseDto> toggleLike(@PathVariable Long postId, HttpServletRequest request) {
        // 세션에서 user 가져오기
        UserEntity user = (UserEntity) request.getSession().getAttribute("user");

        // 예외 처리: 사용자 세션이 없는 경우
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MsgResponseDto("로그인 해주세요.", HttpStatus.UNAUTHORIZED.value()));
        }

        // 좋아요 처리
        boolean isLiked = postService.toggleLike(user, postId);

        // 상태에 따른 메시지 생성
        String message = isLiked ? "좋아요를 눌렀습니다." : "좋아요를 취소하였습니다.";
        MsgResponseDto responseDto = new MsgResponseDto(message, HttpStatus.OK.value());

        return ResponseEntity.ok(responseDto);
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
