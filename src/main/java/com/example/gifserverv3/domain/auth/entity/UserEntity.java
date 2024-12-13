package com.example.gifserverv3.domain.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@DynamicUpdate
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userid")
    private Long userId;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    // 비밀번호 변경 메서드
    public UserEntity updatePassword(String newPassword) {
        return UserEntity.builder()
                .userId(this.userId)
                .username(this.username)
                .email(this.email)
                .password(newPassword) // 변경된 비밀번호 적용
                .build();
    }

}
