package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.User;
import com.wordgarden.wordgarden.entity.Wqwrong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WqwrongRepository extends JpaRepository<Wqwrong, Long> {
    List<Wqwrong> findByUserUid(String uid);
    void deleteByUser(User user);
    boolean existsByUserAndWordWordId(User user, String wordId);
}
