package com.example.gifserverv3.domain.auth.repository;

import com.example.gifserverv3.domain.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByName(String name);
    boolean existsByName(String name);
    boolean existsByEmail(String email);
}
