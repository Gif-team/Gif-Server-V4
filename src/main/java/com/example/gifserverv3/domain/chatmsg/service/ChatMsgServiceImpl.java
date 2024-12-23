package com.example.gifserverv3.domain.chatmsg.service;




import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.gifserverv3.domain.auth.entity.UserEntity;
import com.example.gifserverv3.domain.auth.repository.UserRepository;
import com.example.gifserverv3.domain.chatmsg.dto.ChatMsgDto;
import com.example.gifserverv3.domain.chatmsg.entity.ChatMsgEntity;
import com.example.gifserverv3.domain.chatmsg.entity.request.ChatMsgRequest;
import com.example.gifserverv3.domain.chatmsg.entity.response.ChatMsgResponse;
import com.example.gifserverv3.domain.chatmsg.repository.ChatMsgCustomRepository;
import com.example.gifserverv3.domain.chatmsg.repository.ChatMsgRepository;
import com.example.gifserverv3.domain.chatroom.entity.ChatRoomEntity;
import com.example.gifserverv3.domain.chatroom.repository.ChatRoomRepository;
import com.example.gifserverv3.domain.userchatroom.entity.UserChatRoomEntity;
import com.example.gifserverv3.domain.userchatroom.repository.UserChatRoomRepository;
import com.example.gifserverv3.global.kafka.Producers;
import com.example.gifserverv3.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.gifserverv3.global.type.ErrorCode.NOT_FOUND_ROOM;
import static com.example.gifserverv3.global.type.ErrorCode.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
public class ChatMsgServiceImpl implements ChatMsgService {

    private final ChatMsgRepository chatMsgRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMsgCustomRepository chatMsgCustomRepository;

    private final UserChatRoomRepository userChatRoomRepository;

    private final String BASIC_TOPIC = "CHAT_ROOM";
    private final Producers producers;

    @Override
    public ChatMsgResponse sendMessage(ChatMsgRequest message, Long userId, Long roomId) {
        UserEntity findUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        ChatRoomEntity chatRoom = chatRoomRepository.findChatRoomById(roomId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_ROOM));
        // 채팅 메시지 생성
        ChatMsgEntity chatMsg = ChatMsgEntity.builder()
                .message(message.getMessage())
                .sendTime(LocalDateTime.now())
                .user(findUser)
                .chatRoom(chatRoom)
                .build();
        // Response
        ChatMsgResponse response = ChatMsgResponse.builder()
                .roomId(roomId)
                .from(findUser.getUsername())
                .message(message.getMessage())
                .sendTime(chatMsg.getSendTime())
                .build();
        chatMsgRepository.save(chatMsg);
        String topicName = BASIC_TOPIC + chatRoom.getId();
        producers.produceMessage(topicName, message.getMessage());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMsgDto> getRoomChatMsgList(Long roomId, Long userId, Long lastId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        List<ChatMsgEntity> chatMsgsList = chatMsgCustomRepository.findChatRoomIdByChatMsg(roomId, lastId);
        Optional<UserChatRoomEntity> joinUser = userChatRoomRepository.findByUserId(userId);
        if (joinUser.isEmpty()) {
            throw new CustomException(NOT_FOUND_USER);
        }
        LocalDateTime joinDt = joinUser.get().getJoinDt();
        List<ChatMsgDto> chatMsgDtos = new ArrayList<>();
        for (ChatMsgEntity chatMsg : chatMsgsList) {
            ChatMsgDto build = ChatMsgDto.builder()
                    .chatMsgId(chatMsg.getId())
                    .username(chatMsg.getUser().getUsername())
                    .sendTime(chatMsg.getSendTime())
                    .message(chatMsg.getMessage())
                    .userId(chatMsg.getUser().getId())
                    .build();
            if (build.getSendTime().isAfter(joinDt)) {
                chatMsgDtos.add(build);
            }
        }
        return chatMsgDtos;
    }
}
