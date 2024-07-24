package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.Sq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SqRepository extends JpaRepository<Sq, Long> {
}
