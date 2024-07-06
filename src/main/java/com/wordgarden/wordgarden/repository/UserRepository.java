package com.wordgarden.wordgarden.repository;

/*
유저 데이터 조회
 */

import com.wordgarden.wordgarden.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User>findByUid(String uid);
}
