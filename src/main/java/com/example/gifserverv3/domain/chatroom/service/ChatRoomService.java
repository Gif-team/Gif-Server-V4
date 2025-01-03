package com.example.gifserverv3.domain.chatroom.service;

import com.example.gifserverv3.domain.chatroom.controller.request.ChatRoomRequest;
import com.example.gifserverv3.domain.chatroom.controller.request.ChatRoomUpdateRequest;
import com.example.gifserverv3.domain.chatroom.dto.ChatRoomDto;
import com.example.gifserverv3.domain.chatroom.dto.ChatRoomUserDto;
import com.example.gifserverv3.domain.chatroom.entity.ChatRoomEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatRoomService {

    /**
     * 채팅방 생성
     */
    ChatRoomEntity createRoom(ChatRoomRequest chatRoomRequest, Long userId);

    /**
     *  채팅방 입장
     */
    void joinRoom(Long roomId, Long userId, ChatRoomRequest chatRoomRequest);

    /**
     * 채팅방 전체 조회
     */
    List<ChatRoomDto> getRoomList(Pageable pageable);

    /**
     * 채팅방 사용자가 생성한 기준 조회
     */
    List<ChatRoomDto> roomsByCreatorUser(Long userId, Pageable pageable);

    /**
     * 채팅방 사용자가 참여한 기준 조회
     */
    List<ChatRoomDto> getUserByRoomPartList(Long userId, Pageable pageable);

    /**
     * 채팅방 나가기
     */
    void outRoom(Long userId, Long roomId);

    /**
     * 채팅방 삭제 (단, 채팅방 생성자만 가능)
     */
    void deleteRoom(Long userId, Long roomId);

    /**
     * 채팅방 업데이트 (제목)
     */
    void updateRoom(Long roomId, ChatRoomUpdateRequest chatRoomUpdateRequest, Long userId);

    /**
     * 채팅방에 들어와 있는 유저들 조회
     */
    List<ChatRoomUserDto> getRoomUsers(Long roomId, Long userId);

    /**
     * 채팅방 제목으로 검색 (문자열 앞단, 중단 기준 검색 가능)
     */
    List<ChatRoomDto> searchRoomByTitle(String roomName, Long userId, Pageable pageable);
}
