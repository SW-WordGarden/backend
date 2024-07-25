package com.wordgarden.wordgarden.repository;
import com.wordgarden.wordgarden.entity.Sqinfo;
import com.wordgarden.wordgarden.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SqinfoRepository extends JpaRepository<Sqinfo, Long> {
    @Query("SELECT s.sqTitle FROM Sqinfo s WHERE s.user.uid = :uid ORDER BY s.sqTitle ASC")
    List<String> findTitlesByUserUid(@Param("uid") String uid);

    Optional<Sqinfo> findByUserAndSqTitle(User user, String sqTitle);
    boolean existsByUserAndSqTitle(User user, String sqTitle);

    Optional<Sqinfo> findBySqTitle(String sqTitle);
}
