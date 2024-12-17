package com.example.gifserverv3.domain.badge.service;

import com.example.gifserverv3.domain.badge.entity.BadgeEntity;
import com.example.gifserverv3.domain.badge.repository.BadgeRepository;
import com.example.gifserverv3.domain.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class BadgeService {

    private final PostRepository postRepository;
    private final BadgeRepository badgeRepository;

    public BadgeService(PostRepository postRepository, BadgeRepository badgeRepository) {
        this.postRepository = postRepository;
        this.badgeRepository = badgeRepository;
    }

    @Transactional
    public Map<String, Boolean> updateBadgesForUser(Long userId) {
        // category == true 게시물 개수
        long trueCategoryCount = postRepository.countTrueCategoryPostsByUserId(userId);

        // category == false 게시물 개수
        long falseCategoryCount = postRepository.countFalseCategoryPostsByUserId(userId);

        // 유저의 기존 뱃지 상태 조회 또는 초기화
        BadgeEntity badge = badgeRepository.findByUserId(userId).orElse(
                BadgeEntity.builder().userId(userId).build());

        // 조건에 따라 뱃지 상태 업데이트
        boolean badge1Active = trueCategoryCount >= 10;
        boolean badge2Active = falseCategoryCount >= 10;
        boolean badge3Active = trueCategoryCount >= 20 && falseCategoryCount >= 20;

        badge.setBadge1Active(badge1Active); // 1번 뱃지
        badge.setBadge2Active(badge2Active); // 2번 뱃지
        badge.setBadge3Active(badge3Active); // 3번 뱃지

        // DB에 저장
        badgeRepository.save(badge);

        // 뱃지 상태를 Map으로 반환
        Map<String, Boolean> badgeStatus = new HashMap<>();
        badgeStatus.put("badge1", badge1Active);
        badgeStatus.put("badge2", badge2Active);
        badgeStatus.put("badge3", badge3Active);

        return badgeStatus;
    }
}
