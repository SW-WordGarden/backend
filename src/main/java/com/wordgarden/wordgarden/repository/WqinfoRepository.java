package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.Wqinfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WqinfoRepository  extends JpaRepository<Wqinfo, String> {
}
