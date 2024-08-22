package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.Alarm;
import com.wordgarden.wordgarden.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface AlarmRepository extends JpaRepository<Alarm, String> {
    @Query("SELECT COALESCE(MAX(a.sequence), 0) FROM Alarm a WHERE a.toUser.uid = :userId")
    Long findMaxSequenceByUserId(@Param("userId") String userId);

    @Query("SELECT a FROM Alarm a WHERE a.toUser.uid = :userId ORDER BY a.createTime DESC")
    List<Alarm> findTop30ByToUserOrderByCreateTimeDesc(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT a FROM Alarm a JOIN FETCH a.fromUser WHERE a.alarmId = :alarmId")
    Optional<Alarm> findByIdWithFromUser(@Param("alarmId") String alarmId);

    @Query("SELECT a FROM Alarm a JOIN FETCH a.fromUser JOIN FETCH a.toUser WHERE a.toUser.uid = :userId ORDER BY a.createTime DESC")
    List<Alarm> findTop30ByAlram(@Param("userId") String userId, Pageable pageable);

}