package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUid(String uid);
    boolean existsByuUrl(String uUrl);
    Optional<User> findByuUrl(String uUrl);
}
