package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.Sqresult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SqresultRepository extends JpaRepository<Sqresult, Long> {
    @Query("SELECT sr FROM Sqresult sr JOIN sr.sqinfo si WHERE sr.user.uid = :uid ORDER BY si.sqTitle, sr.sqQnum")
    List<Sqresult> findSolvedQuizzesByUser(@Param("uid") String uid);
}
