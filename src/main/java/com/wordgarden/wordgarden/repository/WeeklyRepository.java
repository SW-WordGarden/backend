package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.Weekly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface WeeklyRepository extends JpaRepository<Weekly, Long> {
    @Transactional
    void deleteByCreatedAtBefore(LocalDateTime dateTime);
}
