package com.example.gifserverv3.domain.auth.repository;

import java.util.List;
import java.util.Optional;

import com.example.gifserverv3.domain.auth.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findUserByEmailAndPassword(String email, String password);

    @Query("select u from UserEntity u where u.id in (:ids) order by u.status desc ")
    List<UserEntity> findUserById(@Param("ids") List<Long> ids, Pageable pageable);

    @Query("SELECT u FROM UserEntity u WHERE u.username LIKE :username")
    Optional<UserEntity> findByUsernameLike(@Param ("username")String username);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
