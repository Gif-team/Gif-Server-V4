package com.example.gifserverv3.domain.auth.service;

import com.example.gifserverv3.domain.auth.dto.UserDto;
import com.example.gifserverv3.domain.auth.entity.UserEntity;
import com.example.gifserverv3.domain.auth.entity.request.LoginRequest;
import com.example.gifserverv3.domain.auth.entity.request.UpdateRequest;
import com.example.gifserverv3.domain.auth.entity.request.UserRequest;
import com.example.gifserverv3.domain.auth.entity.response.UserInfoResponse;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    /**
     * 사용자 회원가입
     */
    UserDto signUp(UserRequest user);

    /**
     * 로그인
     */
    UserEntity login(LoginRequest user);

    /**
     * 사용자 업데이트 (username)
     */
    void update(Long userId, UpdateRequest updateRequest);

    /**
     * 로그아웃
     */
    void logout(Long userId);

    /**
     * 사용자 찾기 (session)
     */
    UserInfoResponse getUserInfoById(Long userId);
}

