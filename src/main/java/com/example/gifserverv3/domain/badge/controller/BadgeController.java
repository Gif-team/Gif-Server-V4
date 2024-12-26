package com.example.gifserverv3.domain.badge.controller;

import com.example.gifserverv3.domain.auth.entity.UserEntity;
import com.example.gifserverv3.domain.badge.service.BadgeServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/badge")
public class BadgeController {

    private final BadgeServiceImpl badgeService;

    public BadgeController(BadgeServiceImpl badgeService) {
        this.badgeService = badgeService;
    }

    @GetMapping
    public ResponseEntity<?> getUserBadges(HttpSession session) {
        // 세션에서 UserEntity를 가져오기
        UserEntity user = (UserEntity) session.getAttribute("user");

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인 해주세요."); // 세션이 없으면 로그인 요청
        }

        // UserEntity에서 userId를 가져오기
        Long userId = user.getId();

        // 유저의 뱃지 상태를 반환
        Map<String, Boolean> badges = badgeService.updateBadgesForUser(userId);

        return ResponseEntity.ok(badges);
    }



}
