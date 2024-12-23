package com.example.gifserverv3.domain.chatroom.entity;


import com.example.gifserverv3.domain.chatmsg.entity.ChatMsgEntity;
import com.example.gifserverv3.domain.userchatroom.entity.UserChatRoomEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chatRooms")
public class ChatRoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    private String title;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long roomCreator;

    private Integer userCountMax; // 최대 인원 8명

    private String password;

    private Boolean isPrivate;


    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY)
    private List<UserChatRoomEntity> userChatRooms;

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY)
    private List<ChatMsgEntity> chatMsgs;

    @Override
    public String toString() {
        return "ChatRoomEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", roomCreator=" + roomCreator +
                ", userCountMax=" + userCountMax +
                '}';
    }

    public void update(String title, LocalDateTime updatedAt) {
        this.setTitle(title);
        this.setUpdatedAt(updatedAt);
    }
}
