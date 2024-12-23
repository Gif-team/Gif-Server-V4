package com.example.gifserverv3.domain.chatroom.repository;


import com.example.gifserverv3.domain.chatroom.entity.ChatRoomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {

    Optional<ChatRoomEntity> findById(Long aLong);

    Optional<ChatRoomEntity> findChatRoomById(Long id);

    @Query("select c from ChatRoomEntity c where c.roomCreator=:userId")
    Page<ChatRoomEntity> findAllByUserId(@Param("userId")Long userId,Pageable pageable); //

    Page<ChatRoomEntity> findAllByUserChatRoomsUserId(Long userId,Pageable pageable);

    Page<ChatRoomEntity> findByTitleContaining(String title, Pageable pageable);
}
