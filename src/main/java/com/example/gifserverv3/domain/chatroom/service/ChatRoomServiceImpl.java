package com.example.gifserverv3.domain.chatroom.service;

import static com.example.gifserverv3.global.type.ErrorCode.*;
import static java.time.LocalDateTime.now;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.example.gifserverv3.domain.auth.entity.UserEntity;
import com.example.gifserverv3.domain.auth.repository.UserRepository;
import com.example.gifserverv3.domain.chatmsg.repository.ChatMsgRepository;
import com.example.gifserverv3.domain.chatroom.controller.request.ChatRoomRequest;
import com.example.gifserverv3.domain.chatroom.controller.request.ChatRoomUpdateRequest;
import com.example.gifserverv3.domain.chatroom.dto.ChatRoomDto;
import com.example.gifserverv3.domain.chatroom.dto.ChatRoomUserDto;
import com.example.gifserverv3.domain.chatroom.entity.ChatRoomEntity;
import com.example.gifserverv3.domain.chatroom.repository.ChatRoomRepository;
import com.example.gifserverv3.domain.userchatroom.entity.UserChatRoomEntity;
import com.example.gifserverv3.domain.userchatroom.repository.UserChatRoomRepository;
import com.example.gifserverv3.global.exception.CustomException;
import com.example.gifserverv3.global.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    private final ChatMsgRepository chatMsgRepository;

    private final UserRepository userRepository;

    private final UserChatRoomRepository userChatRoomRepository;

    private final RedissonClient redissonClient;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String BASIC_TOPIC = "CHAT_ROOM";

    @Override
    @Transactional
    public ChatRoomEntity createRoom(ChatRoomRequest chatRoomRequest, Long userId) {
        // 유저정보조회
        UserEntity findUser = getUser(userId);
        String password = chatRoomRequest.getPassword();
        // chatroom 생성
        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .roomCreator(findUser.getId())
                .title(chatRoomRequest.getTitle())
                .userCountMax(chatRoomRequest.getUserCountMax())
                .createdAt(now())
                .updatedAt(now())
                .isPrivate(false)
                .build();
        if (password != null) {
            chatRoom.setPassword(password);
            chatRoom.setIsPrivate(true);
        }
        ChatRoomEntity save = chatRoomRepository.save(chatRoom);

        // 연관관계 user_chat room 생성
        UserChatRoomEntity userChatRoom = UserChatRoomEntity.builder()
                .user(findUser)
                .chatRoom(save)
                .joinDt(now())
                .build();
        System.out.println("---------------------------------");
        System.out.println("userChatRoom = " + userChatRoom.getUser());
        System.out.println("userChatRoom = " + userChatRoom.getChatRoom());
        System.out.println("---------------------------------");
        // save
        userChatRoomRepository.save(userChatRoom);
        String topicName = BASIC_TOPIC + save.getId();
        NewTopic newTopic = new NewTopic(topicName, 1, (short) 1);
        // Kafka Topic에 구독자 추가
        kafkaTemplate.send(newTopic.name(), "Subscribed");
        return save;
    }

    @Override
    @Transactional
    public void joinRoom(Long roomId, Long userId, ChatRoomRequest chatRoomRequest) {
        RLock lock = redissonClient.getLock("joinRoomLock:" + roomId);
        try {
            boolean available = lock.tryLock(1, TimeUnit.SECONDS);

            if (!available) {
                throw new CustomException(FAILED_GET_LOCK);
            }
            // 유저 조회
            UserEntity findUser = getUser(userId);

            // room 조회
            ChatRoomEntity chatRoom = chatRoomRepository.findById(roomId) // lock (기존)
                    .orElseThrow(() -> new CustomException(NOT_FOUND_ROOM));

            // user_chatroom 현재 인원 카운트 (비즈니스 로직)
            Long currentUserCount = userChatRoomRepository.countNonLockByChatRoomId(roomId); // lock (기존)

            if (chatRoom.getIsPrivate() && chatRoomRequest.getPassword() == null) {
                throw new CustomException(NEED_TO_PASSWORD);
            }
            if (chatRoom.getIsPrivate() && !chatRoomRequest.getPassword().equals(chatRoom.getPassword())) {
                throw new CustomException(ROOM_PASSWORD_MISMATCH);
            }

            if (!chatRoom.getIsPrivate() && chatRoom.getPassword() == null) {
                List<Long> userList = userChatRoomRepository
                        .findUserChatRoomByChatRoom_Id(roomId);
                if (userList.contains(userId)) {
                    throw new CustomException(ALREADY_JOIN_ROOM);
                }

                // chatroom 입장
                if (currentUserCount >= chatRoom.getUserCountMax()) {
                    throw new CustomException(ErrorCode.ROOM_USER_FULL);
                }
                // 비밀번호 확인

                UserChatRoomEntity userChatRoom = UserChatRoomEntity.builder()
                        .user(findUser)
                        .chatRoom(chatRoom)
                        .joinDt(now())
                        .build();
                UserChatRoomEntity save = userChatRoomRepository.save(userChatRoom);
                String topicName = BASIC_TOPIC + save.getChatRoom().getId();
                kafkaTemplate.send(topicName, "Subscribed"); // 개선점
                // 비즈니스 로직 끝
            }
        } catch (InterruptedException e) {
            throw new CustomException(FAILED_GET_LOCK);
        } finally {
            lock.unlock();
        }
    }

    // 채팅방 전체 조회
    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomDto> getRoomList(Pageable pageable) {
        Page<ChatRoomEntity> all = chatRoomRepository.findAll(pageable);
        return getChatRoomDtos(all);
    }

    // 자신이 생성한 방 리스트 조회
    @Override
    public List<ChatRoomDto> roomsByCreatorUser(Long userId, Pageable pageable) {
        Page<ChatRoomEntity> userCreateAll = chatRoomRepository.findAllByUserId(userId, pageable);
        return getChatRoomDtos(userCreateAll);
    }
    // 자신이 참여한 방 리스트 조회

    @Override
    public List<ChatRoomDto> getUserByRoomPartList(Long userId, Pageable pageable) {
        Page<ChatRoomEntity> userPartAll = chatRoomRepository
                .findAllByUserChatRoomsUserId(userId, pageable);
        return getChatRoomDtos(userPartAll);
    }

    @Override
    @Transactional
    public void outRoom(Long userId, Long roomId) {
        ChatRoomEntity room = getChatRoom(roomId);
        List<UserChatRoomEntity> userByChatRoomId = userChatRoomRepository
                .findUserByChatRoomId(roomId);
        List<Long> userIds = new ArrayList<>();
        for (UserChatRoomEntity userChatRoom : userByChatRoomId) {
            Long id = userChatRoom.getUser().getId();
            userIds.add(id);
        }
        // 만약 방에 없는데 나가기를 시도한 경우
        if (!userIds.contains(userId)) {
            throw new CustomException(INVALID_REQUEST);
        }
        // 방장이 아니라면
        if (!Objects.equals(room.getRoomCreator(), userId)) {
            userChatRoomRepository.deleteUserChatRoomByUserId(userId); // point select???
            return;
        }
        // 방장이라면 방 삭제
        chatMsgRepository.deleteChatMsgByChatRoom_Id(roomId);
        userChatRoomRepository.deleteUserChatRoomByChatRoom_Id(roomId);
        chatRoomRepository.deleteById(roomId);
    }

    @Override
    @Transactional
    public void deleteRoom(Long userId, Long roomId) {
        ChatRoomEntity room = getChatRoom(roomId);
        if (!Objects.equals(room.getRoomCreator(), userId)) {
            throw new CustomException(NOT_ROOM_CREATOR);
        }
        chatMsgRepository.deleteChatMsgByChatRoom_Id(roomId);
        userChatRoomRepository.deleteUserChatRoomByChatRoom_Id(roomId);
        chatRoomRepository.deleteById(roomId);
    }

    @Override
    public void updateRoom(Long roomId, ChatRoomUpdateRequest chatRoomUpdateRequest, Long userId) {
        ChatRoomEntity room = getChatRoom(roomId);
        String currentRoomTitle = room.getTitle();
        if (!room.getRoomCreator().equals(userId)) {
            throw new CustomException(NOT_ROOM_CREATOR);
        }
        if (currentRoomTitle.equals(chatRoomUpdateRequest.getTitle())) {
            throw new CustomException(REQUEST_SAME_AS_CURRENT_TITLE);
        }
        room.update(chatRoomUpdateRequest.getTitle(), now());
        chatRoomRepository.save(room);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomUserDto> getRoomUsers(Long roomId, Long userId) {
        // 방 정보
        getChatRoom(roomId);
        // 로그인 유저 정보
        getUser(userId);
        // 방에 있는 유저 정보
        List<UserChatRoomEntity> userIds = userChatRoomRepository
                .findUserChatRoomByChatRoomId(roomId);
        // 방에 있지 않은 유저는 볼 수 없음
        List<Long> userIdList = new ArrayList<>();
        for (UserChatRoomEntity chatRoom : userIds) {
            Long id = chatRoom.getUser().getId();
            userIdList.add(id);
        }
        if (!userIdList.contains(userId)) {
            throw new CustomException(NOT_ROOM_MEMBER);
        }
        // DTO 담기
        List<ChatRoomUserDto> chatRoomUserDtos = new ArrayList<>();
        for (UserChatRoomEntity userChatRoom : userIds) {
            ChatRoomUserDto build = ChatRoomUserDto.builder()
                    .username(userChatRoom.getUser().getUsername())
                    .status(userChatRoom.getUser().getStatus())
                    .build();
            chatRoomUserDtos.add(build);
        }
        return chatRoomUserDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomDto> searchRoomByTitle(String roomName, Long userId, Pageable pageable) {
        getUser(userId);
        Page<ChatRoomEntity> search = chatRoomRepository.findByTitleContaining(roomName, pageable);

        List<ChatRoomEntity> searchRoomList = search.toList();
        List<ChatRoomDto> chatRoomDtos = new ArrayList<>();
        if (searchRoomList.size() == 0) {
            throw new CustomException(NOT_FOUND_ROOM);
        }

        for (ChatRoomEntity chatRoom : searchRoomList) {
            ChatRoomDto build = ChatRoomDto.builder()
                    .id(chatRoom.getId())
                    .title(chatRoom.getTitle())
                    .currentUserCount((long) chatRoom.getUserChatRooms().size())
                    .userCountMax(chatRoom.getUserCountMax())
                    .build();
            chatRoomDtos.add(build);
        }
        return chatRoomDtos;
    }

    private UserEntity getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
    }

    private ChatRoomEntity getChatRoom(Long roomId) {
        return chatRoomRepository
                .findChatRoomById(roomId)
                .orElseThrow(() -> new CustomException(NONE_ROOM));
    }

    // 방 조회 DTO 변환 메서드 추출
    private static List<ChatRoomDto> getChatRoomDtos(Page<ChatRoomEntity> all) {
        return all.stream()
                .map(ChatRoomDto::of)
                .collect(Collectors.toList());
    }
}