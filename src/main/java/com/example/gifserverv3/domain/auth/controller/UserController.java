package com.example.gifserverv3.domain.auth.controller;

import com.example.gifserverv3.domain.auth.dto.UserDto;
import com.example.gifserverv3.domain.auth.entity.UserEntity;
import com.example.gifserverv3.domain.auth.entity.request.LoginRequest;
import com.example.gifserverv3.domain.auth.entity.request.UpdateRequest;
import com.example.gifserverv3.domain.auth.entity.request.UserRequest;
import com.example.gifserverv3.domain.auth.entity.response.UserInfoResponse;
import com.example.gifserverv3.domain.auth.service.UserService;
import com.example.gifserverv3.global.login.LoginCheck;
import com.example.gifserverv3.global.util.ResponseUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.gifserverv3.global.type.ResponseMessage.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Object> signUp(
            @RequestBody @Valid UserRequest userRequest) {
        UserDto user = userService.signUp(userRequest);
        return ResponseUtils.ok(CREATE_USER, user);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(
            @RequestBody @Valid LoginRequest userRequest,
            HttpSession session) {
        UserEntity login = userService.login(userRequest);
        session.setAttribute("user", login.getId());
        return ResponseUtils.ok(LOGIN_SUCCESS);
    }

    @PostMapping("/logout")
    @LoginCheck
    public ResponseEntity<Object> logout(HttpSession session) {
        Long userId = (Long) session.getAttribute("user");
        userService.logout(userId);
        session.invalidate();
        return ResponseUtils.ok(LOGOUT_SUCCESS);
    }

    @PatchMapping("/update")
    @LoginCheck
    public ResponseEntity<Object> update(
            @RequestBody @Valid UpdateRequest updateRequest,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("user");
        userService.update(userId, updateRequest);
        return ResponseUtils.ok(USER_UPDATE_SUCCESS);
    }

    @GetMapping("/user")
    @LoginCheck
    public ResponseEntity<Object> getUserInfo(HttpSession session) {
        Long userId = (Long) session.getAttribute("user");
        UserInfoResponse userInfo = userService.getUserInfoById(userId);
        return ResponseUtils.ok(USER_SEARCH_SUCCESS, userInfo);
    }
}
