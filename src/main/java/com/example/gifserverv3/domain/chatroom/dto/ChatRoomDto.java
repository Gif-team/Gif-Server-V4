package com.example.gifserverv3.domain.chatroom.dto;

import com.example.gifserverv3.domain.chatroom.entity.ChatRoomEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomDto {
    private Long id;

    private String title;

    private Long currentUserCount;

    private Integer userCountMax;

    private Integer heartCount;

    public static ChatRoomDto of(ChatRoomEntity chatRoom) {
        return ChatRoomDto.builder()
                .id(chatRoom.getId())
                .title(chatRoom.getTitle())
                .currentUserCount((long) chatRoom.getUserChatRooms().size())
                .userCountMax(chatRoom.getUserCountMax())
                .build();
    }
}
