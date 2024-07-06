package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, String> {
    List<Word> findByCategory(String category);
}