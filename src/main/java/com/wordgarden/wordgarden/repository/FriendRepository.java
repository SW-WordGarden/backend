package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.Friend;
import com.wordgarden.wordgarden.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findAllByUser(User user);

    boolean existsByUserAndFriendId(User user, String friendId);

    Optional<Friend> findByUserAndFriendId(User user, String friendId);

    @Modifying
    @Query("DELETE FROM Friend f WHERE f.user.uid = :uid OR f.friendId = :uid")
    void deleteByUserUidOrFriendId(@Param("uid") String uid);
}