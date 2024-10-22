package com.example.gifserverv3.domain.chat.repository;

import com.example.gifserverv3.domain.chat.entity.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<ChatEntity, Long> {
    List<ChatEntity> findAllByRoomId(Long roomId);
}
