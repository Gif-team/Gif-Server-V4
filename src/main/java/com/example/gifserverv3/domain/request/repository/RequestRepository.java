package com.example.gifserverv3.domain.request.repository;

import com.example.gifserverv3.domain.auth.entity.UserEntity;
import com.example.gifserverv3.domain.request.entity.RequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<RequestEntity, Long> {
    boolean existsByRequestId(Long requestId);
    void deleteByRequestId(Long requestId);
    Integer countByAuthor(UserEntity user);
}
