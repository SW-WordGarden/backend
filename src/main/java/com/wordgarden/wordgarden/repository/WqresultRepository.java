package com.wordgarden.wordgarden.repository;


import com.wordgarden.wordgarden.entity.Wqresult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WqresultRepository extends JpaRepository<Wqresult, Long> {
}
