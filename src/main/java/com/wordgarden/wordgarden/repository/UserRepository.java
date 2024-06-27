package com.wordgarden.wordgarden.repository;


import com.wordgarden.wordgarden.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
