package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    // Uid 찾기
    Optional<User> findByUid(String uid);

    // 사용자 친구 추가용 아이디 확인
    boolean existsByuUrl(String uUrl);
    Optional<User> findByuUrl(String uUrl);

    // 모든 사용자 단어 퀴즈 참여 횟수 초기화
    @Modifying
    @Query("UPDATE User u SET u.uParticipate = 0")
    void resetAllUsersParticipation();
}
