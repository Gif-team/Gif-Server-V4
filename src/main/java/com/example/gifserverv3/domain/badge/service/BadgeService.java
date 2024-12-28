package com.example.gifserverv3.domain.badge.service;

import java.util.Map;

public interface BadgeService  {

    /**
     * Badge 상태 확인
     */
    Map<String, Boolean> updateBadgesForUser(Long userId);
}
