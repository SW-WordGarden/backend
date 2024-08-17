package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.Wqinfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WqinfoRepository  extends JpaRepository<Wqinfo, String> {
    List<Wqinfo> findByWqTitle(String wqTitle);

    @Query("SELECT COUNT(w) FROM Wqinfo w WHERE w.wqTitle LIKE CONCAT(:prefix, '%')")
    long countByWqTitlePrefix(@Param("prefix") String prefix);
}
