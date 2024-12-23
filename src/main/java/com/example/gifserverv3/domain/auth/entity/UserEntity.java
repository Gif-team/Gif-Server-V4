package com.example.gifserverv3.domain.auth.entity;

import com.example.gifserverv3.domain.chatmsg.entity.ChatMsgEntity;
import com.example.gifserverv3.domain.userchatroom.entity.UserChatRoomEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@DynamicUpdate
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(name = "status")
    private Boolean status; // 로그인이면 true, 로그아웃 false

    // 비밀번호 변경 메서드
    public UserEntity updatePassword(String newPassword) {
        return UserEntity.builder()
                .id(this.id)
                .username(this.username)
                .email(this.email)
                .password(newPassword) // 변경된 비밀번호 적용
                .build();
    }

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserChatRoomEntity> userChatRooms;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<ChatMsgEntity> chatMsgs;

    public void update(String username, LocalDateTime updatedAt) {
        this.setUsername(username);
        this.setUpdatedAt(updatedAt);
    }

    public UserEntity(Long id) {
        this.id = id;
    }

}
