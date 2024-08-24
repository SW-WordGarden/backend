package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.Learning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningRepository extends JpaRepository<Learning, Long> {
    List<Learning> findByWordEntityCategory(String category);

    @Query(value = "SELECT * FROM learning_tb ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Learning findRandomLearning();
}