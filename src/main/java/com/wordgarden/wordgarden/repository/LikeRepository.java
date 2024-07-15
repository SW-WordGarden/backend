package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    @Query("SELECT l FROM Like l WHERE l.word.wordId = :wordId AND l.userId = :userId")
    Like findByWordIdAndUserId(@Param("wordId") String wordId, @Param("userId") String userId);

    List<Like> findByUserId(String userId); // 사용자가 좋아요를 누른 목록 조회
}
