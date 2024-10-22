package com.example.gifserverv3.domain.chat.repository;

import com.example.gifserverv3.domain.auth.entity.UserEntity;
import com.example.gifserverv3.domain.chat.entity.RoomEntity;
import com.example.gifserverv3.domain.request.entity.RequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
    List<RoomEntity> findByFromUserOrToUser(UserEntity fromUser, UserEntity toUser);
    boolean existsByRequestAndToUser(RequestEntity request, UserEntity toUser);
}
