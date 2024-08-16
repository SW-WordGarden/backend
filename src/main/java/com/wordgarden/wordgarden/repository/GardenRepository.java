package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.Garden;
import com.wordgarden.wordgarden.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GardenRepository extends JpaRepository<Garden, String> {
    // 사용자로 정원을 찾는 메서드
    Optional<Garden> findByUser(User user);
}