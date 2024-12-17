package com.example.gifserverv3.domain.badge.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "badges")
@Getter
@Setter
@NoArgsConstructor
public class BadgeEntity {

    @Id
    private Long userId;

    @Column(nullable = false, name = "badge1")
    private boolean badge1Active; // 1번 뱃지 활성화 여부

    @Column(nullable = false, name = "badge2")
    private boolean badge2Active; // 2번 뱃지 활성화 여부

    @Column(nullable = false, name = "badge3")
    private boolean badge3Active; // 3번 뱃지 활성화 여부

    // Builder 패턴을 사용하여 객체 생성
    @Builder
    public BadgeEntity(Long userId, boolean badge1Active, boolean badge2Active, boolean badge3Active) {
        this.userId = userId;
        this.badge1Active = badge1Active;
        this.badge2Active = badge2Active;
        this.badge3Active = badge3Active;
    }
}
