package com.example.gifserverv3.domain.auth.service;

import java.time.LocalDateTime;
import java.util.Optional;

import com.example.gifserverv3.domain.auth.dto.UserDto;
import com.example.gifserverv3.domain.auth.entity.UserEntity;
import com.example.gifserverv3.domain.auth.entity.request.LoginRequest;
import com.example.gifserverv3.domain.auth.entity.request.UpdateRequest;
import com.example.gifserverv3.domain.auth.entity.request.UserRequest;
import com.example.gifserverv3.domain.auth.entity.response.UserInfoResponse;
import com.example.gifserverv3.domain.auth.repository.UserRepository;
import com.example.gifserverv3.domain.name.service.NameService;
import com.example.gifserverv3.global.exception.CustomException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.gifserverv3.global.type.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HttpSession session;

    @Override
    public UserDto signUp(UserRequest user) {
        String email = user.getEmail();

        // gsm email 검사
        String emailDomainRegex = "gsm.hs.kr";
        int index = email.indexOf("@");
        String emailDomain = email.substring(index + 1);
        if (!emailDomain.equals(emailDomainRegex)) {
            throw new CustomException(NOT_GSM_EMAIL);
        }

        // 사용자 이름 자동 생성 로직
        String username = NameService.getUsernameFromEmail(email);

        // 회원 중복 확인
        boolean isUserNameDuplicated = userRepository.existsByUsername(username);
        boolean isUserEmailDuplicated = userRepository.existsByEmail(email);
        if (isUserNameDuplicated || isUserEmailDuplicated) {
            throw new CustomException(DUPLICATED_USERNAME);
        }

        String userPasswordEncode = passwordEncoder.encode(user.getPassword());

        UserEntity userSave = UserEntity.builder()
                .email(user.getEmail())
                .password(userPasswordEncode)
                .username(username)
                .createdAt(LocalDateTime.now())
                .status(false)
                .build();

        UserDto userDto = UserDto.builder()
                .email(userSave.getEmail())
                .username(username)
                .build();
        try {
            userRepository.save(userSave);
        } catch (DuplicateKeyException e) {
            throw new CustomException(ALREADY_USER_USERNAME);
        }
        return userDto;
    }

    @Override
    public UserEntity login(LoginRequest request) {
        String requestPassword = request.getPassword(); // 1244
        Optional<UserEntity> user = userRepository.findByEmail(request.getEmail());// db pw
        if (user.isEmpty()) {
            throw new CustomException(NOT_FOUND_USER, request.getEmail());
        }
        String dbPassword = user.get().getPassword();
        if (!isSamePassword(requestPassword, dbPassword)) {
            throw new CustomException(NOT_SAME_PASSWORD);
        }
        user.get().setStatus(true);
        userRepository.save(user.get());
        return userRepository.findUserByEmailAndPassword(request.getEmail(), dbPassword).orElseThrow();
    }

    @Override
    public void update(Long userId, UpdateRequest updateRequest) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        Optional<UserEntity> byNickname = userRepository.
                findByUsername(updateRequest.getUsername());
        if (byNickname.isPresent()) {
            throw new CustomException(ALREADY_USER_USERNAME, updateRequest.getUsername());
        }
        String currentNickname = user.getUsername();
        if (currentNickname.equals(updateRequest.getUsername())) {
            throw new CustomException(REQUEST_SAME_AS_CURRENT_NICKNAME); // 현재 닉네임과 바꿀 닉네임이 같을 경우
        }
        user.update(updateRequest.getUsername(), LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void logout(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        user.setStatus(false);
        userRepository.save(user);
    }

    @Override
    public UserInfoResponse getUserInfoById(Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER)); // 사용자 ID로 사용자 조회

        return UserInfoResponse.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())  // 예시로 이메일 정보도 포함
                .build();
    }

    public boolean isSamePassword(String password, String dbUserPassword) {
        return passwordEncoder.matches(password, dbUserPassword);
    }
}
