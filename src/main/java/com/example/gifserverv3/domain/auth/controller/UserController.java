package com.example.gifserverv3.domain.auth.controller;

import com.example.gifserverv3.domain.auth.dto.request.ChangePasswordRequest;
import com.example.gifserverv3.domain.auth.dto.request.LoginRequest;
import com.example.gifserverv3.domain.auth.dto.request.SignupRequest;
import com.example.gifserverv3.domain.auth.dto.response.UserInfoResponse;
import com.example.gifserverv3.domain.auth.entity.UserEntity;
import com.example.gifserverv3.domain.auth.repository.UserRepository;
import com.example.gifserverv3.domain.auth.service.UserService;
import com.example.gifserverv3.global.exception.CustomException;
import com.example.gifserverv3.global.util.MsgResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final HttpSession httpSession; // 세션 객체 주입
    private final UserRepository userRepository;

    // 회원 가입
    @PostMapping("/signup")
    public ResponseEntity<MsgResponseDto> signup(@RequestBody @Valid SignupRequest request) {
        userService.signup(request);
        return ResponseEntity.ok(new MsgResponseDto("회원가입 완료", HttpStatus.CREATED.value()));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<MsgResponseDto> login(@RequestBody LoginRequest loginRequestDto, HttpServletRequest request) {
        UserEntity user = userService.login(loginRequestDto);

        // 세션에 사용자 정보 저장
        request.getSession().setAttribute("user", user);

        return ResponseEntity.ok(new MsgResponseDto("로그인 완료", HttpStatus.OK.value()));
    }

    // 회원 탈퇴
    @DeleteMapping("/user/delete")
    public ResponseEntity<?> signOut(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 세션이 존재할 경우 가져옴

        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인 해주세요."); // 세션이 없으면 로그인 요청
        }

        if (session != null) {
            session.invalidate(); // 세션 무효화
        }
        return ResponseEntity.ok(new MsgResponseDto("회원탈퇴 완료", HttpStatus.RESET_CONTENT.value()));
    }

    // 사용자 정보 반환
    @GetMapping("/user")
    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 세션이 존재할 경우 가져옴
        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인 해주세요."); // 세션이 없으면 로그인 요청
        }

        UserEntity user = (UserEntity) session.getAttribute("user"); // 세션에서 사용자 정보 가져오기
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인 해주세요."); // 세션에 사용자 정보가 없으면 로그인 요청
        }
        
        UserInfoResponse userInfoResponse = UserInfoResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();

        return ResponseEntity.ok(userInfoResponse); // 사용자 정보 반환
    }

    // 비밀번호 변경
    @PutMapping("/pwchange")
    public ResponseEntity<MsgResponseDto> changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequestDto, HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 세션이 존재할 경우 가져옴
        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MsgResponseDto("로그인 해주세요.", HttpStatus.UNAUTHORIZED.value()));
        }

        UserEntity user = (UserEntity) session.getAttribute("user"); // 세션에서 사용자 정보 가져오기
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MsgResponseDto("로그인 해주세요.", HttpStatus.UNAUTHORIZED.value()));
        }

        try {
            userService.changePassword(user.getUserId(), changePasswordRequestDto);

            // 세션 무효화
            session.invalidate();

            return ResponseEntity.ok(new MsgResponseDto("비밀번호가 성공적으로 변경되었습니다.", HttpStatus.OK.value()));
        } catch (CustomException ex) {
            // Check for password mismatch error and return a custom message
            if ("NOT_MATCH_INFORMATION".equals(ex.getMessage())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new MsgResponseDto("비밀번호가 일치하지 않습니다", HttpStatus.BAD_REQUEST.value()));
            }

            // Handle other errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MsgResponseDto("비밀번호가 일치하지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}
