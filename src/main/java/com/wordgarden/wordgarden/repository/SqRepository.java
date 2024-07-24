package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.Sq;
import com.wordgarden.wordgarden.entity.Sqinfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SqRepository extends JpaRepository<Sq, Long> {
    List<Sq> findBySqinfoOrderBySqQnumAsc(Sqinfo sqinfo);

}
