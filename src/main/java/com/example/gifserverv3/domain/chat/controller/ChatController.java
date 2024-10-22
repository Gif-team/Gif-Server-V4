package com.example.gifserverv3.domain.chat.controller;

import com.example.gifserverv3.domain.auth.entity.UserEntity;
import com.example.gifserverv3.domain.auth.repository.UserRepository;
import com.example.gifserverv3.domain.chat.dto.request.ChatMessage;
import com.example.gifserverv3.domain.chat.dto.response.ChatResponse;
import com.example.gifserverv3.domain.chat.service.ChatService;
import com.example.gifserverv3.global.exception.CustomException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import static com.example.gifserverv3.global.exception.ErrorCode.INVALID_SESSION;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;

    @MessageMapping("/{roomId}")
    @SendTo("/room/{roomId}")
    public ChatResponse test(@DestinationVariable Long roomId, ChatMessage message, HttpSession session) {
        // 세션에서 사용자 정보 확인
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            throw new CustomException(INVALID_SESSION);  // 세션이 없거나 유효하지 않으면 예외 발생
        }

        // 채팅 메시지 처리
        return chatService.createChat(roomId, user, message.getMessage());
    }
}
