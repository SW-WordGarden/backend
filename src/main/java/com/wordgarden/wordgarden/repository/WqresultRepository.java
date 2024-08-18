package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.Wqresult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface WqresultRepository extends JpaRepository<Wqresult, Long> {

    @Query("SELECT DISTINCT w.wqInfo.wqTitle FROM Wqresult w WHERE w.user.uid = :userId")
    Set<String> findDistinctWqTitlesByUserId(String userId);

    long countByUserUid(String uid);

    long countByUserUidAndWqCheckTrue(String uid);

}
