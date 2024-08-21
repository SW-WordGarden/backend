package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.Sqinfo;
import com.wordgarden.wordgarden.entity.Sqresult;
import com.wordgarden.wordgarden.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SqresultRepository extends JpaRepository<Sqresult, Long> {
    @Query("SELECT DISTINCT sr.sqinfo.sqTitle FROM Sqresult sr WHERE sr.user.uid = :uid")
    List<String> findDistinctSqTitlesByUserUid(@Param("uid") String uid);

    List<Sqresult> findByUser_UidAndSqinfo_SqTitle(String uid, String title);

    List<Sqresult> findBySqinfoAndUserOrderBySqQnumAsc(Sqinfo sqinfo, User user);

    Optional<Sqresult> findTopByUserOrderByTimeDesc(User user);

    List<Sqresult> findByUser_UidAndSqinfo(String uid, Sqinfo sqinfo);

    @Query("SELECT DISTINCT sr.sqinfo.sqId as sqId, sr.sqinfo.sqTitle as title FROM Sqresult sr WHERE sr.user.uid = :uid")
    List<Object[]> findDistinctSqInfoByUserUid(@Param("uid") String uid);;
}
