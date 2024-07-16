package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findByUserUid(String userId);

    @Query("SELECT l FROM Like l WHERE l.user.uid = :userId")
    List<Like> findLikesByUserId(@Param("userId") String userId);

}
