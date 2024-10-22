package com.example.gifserverv3.domain.auth.service;

import com.example.gifserverv3.domain.auth.dto.request.LoginRequest;
import com.example.gifserverv3.domain.auth.dto.request.SignOutRequest;
import com.example.gifserverv3.domain.auth.dto.request.SignupRequest;
import com.example.gifserverv3.domain.auth.dto.response.UserInfoResponse;
import com.example.gifserverv3.domain.auth.repository.UserRepository;
import com.example.gifserverv3.global.exception.CustomException;
import com.example.gifserverv3.domain.auth.entity.UserEntity;
import static com.example.gifserverv3.global.exception.ErrorCode.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@Validated
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    public void signup(SignupRequest signupRequestDto) {
        String username = signupRequestDto.getUsername();
        String email = signupRequestDto.getEmail();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());

        String emailIdRegex = "gsm.hs.kr";

        // 회원 중복 확인
        boolean isUserNameDuplicated = userRepository.existsByName(username);
        boolean isUserEmailDuplicated = userRepository.existsByEmail(email);
        if (isUserNameDuplicated || isUserEmailDuplicated) {
            throw new CustomException(DUPLICATED_USERNAME);
        }

        int index = email.indexOf("@");
        String emailDomain = email.substring(index + 1);
        if (!emailDomain.equals(emailIdRegex))
            throw new CustomException(NOT_GSM_EMAIL);

        UserEntity user = UserEntity.builder()
                .name(username)
                .password(password)
                .email(signupRequestDto.getEmail())
                .build();
        userRepository.save(user);
    }

    // 로그인
    public UserEntity login(LoginRequest loginRequestDto) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        UserEntity user = userRepository.findByName(username).orElseThrow(
                () -> new CustomException(NOT_MATCH_INFORMATION)
        );

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(NOT_MATCH_INFORMATION);
        }

        return user;
    }

    // 회원탈퇴
    public void signOut(SignOutRequest signOutRequestDto, UserEntity user) {

        // 사용자명 일치 여부 확인
        user = userRepository.findByName(signOutRequestDto.getUsername()).orElseThrow(
                () -> new CustomException(NOT_MATCH_INFORMATION)
        );

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(signOutRequestDto.getPassword(), user.getPassword())) {
            throw new CustomException(NOT_MATCH_INFORMATION);
        }

        userRepository.deleteById(user.getUsersId());
    }

    // 유저 정보 반환
    @Transactional
    public UserInfoResponse findUserInfo(UserEntity user) {
        UserEntity toDtoUser = userRepository.findById(user.getUsersId()).orElseThrow(() -> new CustomException(NOT_MATCH_INFORMATION));
        return UserInfoResponse.builder()
                .id(toDtoUser.getUsersId())
                .username(toDtoUser.getName())
                .build();
    }
}
