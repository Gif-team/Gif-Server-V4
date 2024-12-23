package com.example.gifserverv3.domain.chatmsg.service;


import com.example.gifserverv3.domain.chatmsg.dto.ChatMsgDto;
import com.example.gifserverv3.domain.chatmsg.entity.request.ChatMsgRequest;
import com.example.gifserverv3.domain.chatmsg.entity.response.ChatMsgResponse;

import java.util.List;

public interface ChatMsgService {

    /**
     * 채팅 보내기
     */
    ChatMsgResponse sendMessage(ChatMsgRequest message, Long userId, Long roomId);

    /**
     * 채팅 조회
     */
    List<ChatMsgDto> getRoomChatMsgList(Long roomId, Long userId, Long lastId);
}
