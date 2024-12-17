package com.example.gifserverv3.domain.auth.service;

import com.example.gifserverv3.domain.auth.dto.request.ChangePasswordRequest;
import com.example.gifserverv3.domain.auth.dto.request.LoginRequest;
import com.example.gifserverv3.domain.auth.dto.request.SignOutRequest;
import com.example.gifserverv3.domain.auth.dto.request.SignupRequest;
import com.example.gifserverv3.domain.auth.dto.response.UserInfoResponse;
import com.example.gifserverv3.domain.auth.repository.UserRepository;
import com.example.gifserverv3.global.data.Name;
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

    public void signup(SignupRequest signupRequestDto) {
        String email = signupRequestDto.getEmail();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());

        // 이메일 도메인 체크
        String emailDomainRegex = "gsm.hs.kr";
        int index = email.indexOf("@");
        String emailDomain = email.substring(index + 1);
        if (!emailDomain.equals(emailDomainRegex)) {
            throw new CustomException(NOT_GSM_EMAIL);
        }

        // 이메일 ID 추출 (예: s24021@gsm.hs.kr -> s24021)
        String emailId = email.substring(0, index);

        // 사용자 이름 자동 생성 로직
        String username = Name.getUsernameFromEmail(email);

        // 회원 중복 확인
        boolean isUserNameDuplicated = userRepository.existsByUsername(username);
        boolean isUserEmailDuplicated = userRepository.existsByEmail(email);
        if (isUserNameDuplicated || isUserEmailDuplicated) {
            throw new CustomException(DUPLICATED_USERNAME);
        }

        // UserEntity 생성 및 저장
        UserEntity user = UserEntity.builder()
                .username(username) // 자동 생성된 username
                .password(password)
                .email(email)
                .build();

        userRepository.save(user);
    }

    // 로그인
    public UserEntity login(LoginRequest loginRequestDto) {
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();

        UserEntity user = userRepository.findByEmail(email).orElseThrow(
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
