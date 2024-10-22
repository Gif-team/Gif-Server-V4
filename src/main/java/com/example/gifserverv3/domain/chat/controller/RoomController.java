package com.example.gifserverv3.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import com.example.gifserverv3.domain.chat.dto.request.RoomCreateRequest;
import com.example.gifserverv3.domain.chat.dto.response.ChatResponse;
import com.example.gifserverv3.domain.chat.dto.response.RoomCreateResponse;
import com.example.gifserverv3.domain.chat.dto.response.RoomResponse;
import com.example.gifserverv3.domain.chat.service.ChatService;
import com.example.gifserverv3.domain.auth.entity.UserEntity;
import com.example.gifserverv3.global.util.MsgResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RoomController {

    private final ChatService chatService;

    @PostMapping("/room")
    public ResponseEntity<RoomCreateResponse> createRoom(@RequestBody RoomCreateRequest request, HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 세션이 없으면 401 반환
        }
        return ResponseEntity.ok(chatService.createRoom(user.getUsersId(), request.getRequestId()));
    }

    @GetMapping("/room")
    public ResponseEntity<List<RoomResponse>> findRoomList(HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 세션이 없으면 401 반환
        }
        return ResponseEntity.ok(chatService.roomListFind(user));
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<RoomResponse> findRoom(HttpSession session, @PathVariable Long roomId) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 세션이 없으면 401 반환
        }
        return ResponseEntity.ok(chatService.roomFind(user, roomId));
    }

    @GetMapping("/chat/{roomId}")
    public ResponseEntity<List<ChatResponse>> findChatList(@PathVariable Long roomId, HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 세션이 없으면 401 반환
        }
        return ResponseEntity.ok(chatService.chatListFind(roomId, user));
    }

    @DeleteMapping("/room/{roomId}")
    public ResponseEntity<MsgResponseDto> deleteRoom(@PathVariable Long roomId, HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 세션이 없으면 401 반환
        }
        chatService.roomDelete(roomId, user);
        return ResponseEntity.ok(new MsgResponseDto("채팅방이 삭제되었습니다.", HttpStatus.NO_CONTENT.value()));
    }

    @GetMapping("/")
    public ResponseEntity<Void> health() {
        return ResponseEntity.ok().build();
    }
}
