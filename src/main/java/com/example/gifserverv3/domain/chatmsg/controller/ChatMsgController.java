package com.example.gifserverv3.domain.chatmsg.controller;

import com.example.gifserverv3.domain.chatmsg.dto.ChatMsgDto;
import com.example.gifserverv3.domain.chatmsg.entity.request.ChatMsgRequest;
import com.example.gifserverv3.domain.chatmsg.entity.response.ChatMsgResponse;
import com.example.gifserverv3.domain.chatmsg.service.ChatMsgService;
import com.example.gifserverv3.global.login.LoginCheck;
import com.example.gifserverv3.global.util.ResponseUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static com.example.gifserverv3.global.type.ResponseMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatMsgController {

    private final ChatMsgService chatMsgService;

    @PostMapping("/msg/{roomId}")
    @LoginCheck
    public ResponseEntity<Object> sendChat(
            @PathVariable Long roomId,
            @RequestBody @Valid ChatMsgRequest message,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("user");
        ChatMsgResponse response = chatMsgService.sendMessage(message, userId, roomId);
        return ResponseUtils.ok(SEND_CHAT_MSG_SUCCESS, response);
    }

    @GetMapping("/msg/{roomId}")
    @LoginCheck
    public ResponseEntity<Object> getChatList(
            @PathVariable Long roomId,
            @RequestParam(required = false) Long lastId,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("user");
        List<ChatMsgDto> roomChatMsgList = chatMsgService.getRoomChatMsgList(roomId, userId, lastId);
        if (roomChatMsgList != null) {
            return ResponseUtils
                    .ok(CHAT_ROOM_MSG_LIST_SELECT_SUCCESS, roomChatMsgList);
        } else {
            return ResponseUtils
                    .notFound(NOT_EXIST_CHAT_ROOM_MSG_LIST);
        }
    }
}
