package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.Friend;
import com.wordgarden.wordgarden.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findAllByUser(User user);

    boolean existsByUserAndFriendId(User user, String friendId);

    Optional<Friend> findByUserAndFriendId(User user, String friendId);

}