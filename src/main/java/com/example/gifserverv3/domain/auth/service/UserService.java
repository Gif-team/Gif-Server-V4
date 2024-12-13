package com.example.gifserverv3.domain.auth.service;

import com.example.gifserverv3.domain.auth.dto.request.ChangePasswordRequest;
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
        boolean isUserNameDuplicated = userRepository.existsByUsername(username);
        boolean isUserEmailDuplicated = userRepository.existsByEmail(email);
        if (isUserNameDuplicated || isUserEmailDuplicated) {
            throw new CustomException(DUPLICATED_USERNAME);
        }

        int index = email.indexOf("@");
        String emailDomain = email.substring(index + 1);
        if (!emailDomain.equals(emailIdRegex))
            throw new CustomException(NOT_GSM_EMAIL);

        UserEntity user = UserEntity.builder()
                .username(username)
                .password(password)
                .email(signupRequestDto.getEmail())
                .build();
        userRepository.save(user);
    }

    // 로그인
    public UserEntity login(LoginRequest loginRequestDto) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        UserEntity user = userRepository.findByUsername(username).orElseThrow(
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
        user = userRepository.findByUsername(signOutRequestDto.getUsername()).orElseThrow(
                () -> new CustomException(NOT_MATCH_INFORMATION)
        );

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(signOutRequestDto.getPassword(), user.getPassword())) {
            throw new CustomException(NOT_MATCH_INFORMATION);
        }

        userRepository.deleteById(user.getUserId());
    }

    // 유저 정보 반환
    @Transactional
    public UserInfoResponse findUserInfo(UserEntity user) {
        UserEntity toDtoUser = userRepository.findById(user.getUserId()).orElseThrow(() -> new CustomException(NOT_MATCH_INFORMATION));
        return UserInfoResponse.builder()
                .userId(toDtoUser.getUserId())
                .username(toDtoUser.getUsername())
                .build();
    }

    // 비밀번호 변경
    public void changePassword(Long userId, ChangePasswordRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String password = request.getCurrentPassword();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(NOT_MATCH_INFORMATION);
        }

        // 새 비밀번호를 해싱하여 설정
        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());

        // 새 객체 생성
        UserEntity updatedUser = UserEntity.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(encodedNewPassword) // 해싱된 새 비밀번호 설정
                .build();

        userRepository.save(updatedUser); // DB에 변경 내용 저장
    }
}
