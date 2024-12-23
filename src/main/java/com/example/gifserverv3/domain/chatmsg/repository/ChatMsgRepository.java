package com.example.gifserverv3.domain.chatmsg.repository;

import com.example.gifserverv3.domain.chatmsg.entity.ChatMsgEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMsgRepository extends JpaRepository<ChatMsgEntity, Long> {
    void deleteChatMsgByChatRoom_Id(Long chatRoomId);
}
