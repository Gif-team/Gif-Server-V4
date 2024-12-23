package com.example.gifserverv3.domain.userchatroom.repository;

import java.util.List;
import java.util.Optional;

import com.example.gifserverv3.domain.userchatroom.entity.UserChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserChatRoomRepository extends JpaRepository<UserChatRoomEntity, Long> {

    @Query("select count(*) from UserChatRoomEntity u where u.chatRoom.id = :roomId")
    Long countNonLockByChatRoomId(@Param("roomId")Long roomId); // test 용도

    void deleteUserChatRoomByChatRoom_Id(Long chatRoomId);

    void deleteUserChatRoomByUserId(Long userId);

    @Query("select u.user.id from UserChatRoomEntity u where u.chatRoom.id = ?1")
    List<Long> findUserChatRoomByChatRoom_Id(Long chatRoomId);

    List<UserChatRoomEntity> findUserChatRoomByChatRoomId(Long roomId);

    List<UserChatRoomEntity> findUserByChatRoomId(Long roomId);

    Optional<UserChatRoomEntity> findByUserId(Long userId);;
}
