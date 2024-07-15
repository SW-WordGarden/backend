package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
    List<Word> findByCategory(String category);

    @Query("SELECT DISTINCT w.category FROM Word w")
    List<String> findDistinctCategories();

    List<Word> findTop10ByCategoryOrderById(String category);

    @Query("SELECT w FROM Word w WHERE w.wordId = :wordId")
    Word findByWordId(@Param("wordId") String wordId);

    Optional<Word> findById(String wordId);
}
