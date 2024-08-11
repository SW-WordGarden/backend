package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.Wqinfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WqinfoRepository  extends JpaRepository<Wqinfo, String> {
    List<Wqinfo> findByWqTitle(String wqTitle);
}
