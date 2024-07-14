package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
    List<Word> findByCategory(String category);

    @Query("SELECT DISTINCT w.category FROM Word w")
    List<String> findDistinctCategories();

    List<Word> findTop10ByCategoryOrderById(String category);
}
