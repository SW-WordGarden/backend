package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.Like;
import com.wordgarden.wordgarden.entity.User;
import com.wordgarden.wordgarden.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query("SELECT l FROM Like l WHERE l.user.uid = :userId")
    List<Like> findLikesByUserId(@Param("userId") String userId);

    // 사용자와 단어를 기준으로 좋아요 찾기
    Like findByUserAndWord(User user, Word word);

    // 사용자 ID를 기준으로 사용자의 좋아요 리스트 찾기
    List<Like> findByUserUid(String uid);
}
