package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.Wqwrong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WqwrongRepository extends JpaRepository<Wqwrong, Long> {
    List<Wqwrong> findByUserUid(String uid);
}
