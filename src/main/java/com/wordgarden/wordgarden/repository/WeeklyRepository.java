package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.Weekly;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface WeeklyRepository extends JpaRepository<Weekly, Long> {
    void deleteByCreatedAtBefore(LocalDateTime dateTime);
}
