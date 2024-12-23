package com.example.gifserverv3.domain.chatmsg.repository;


import com.example.gifserverv3.domain.chatmsg.entity.ChatMsgEntity;

import java.util.List;

public interface ChatMsgCustomRepository {
    List<ChatMsgEntity> findChatRoomIdByChatMsg(Long chatMsg, Long lastId);
}
