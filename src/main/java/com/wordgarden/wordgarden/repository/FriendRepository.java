package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.Friend;
import com.wordgarden.wordgarden.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findAllByUser(User user);
}