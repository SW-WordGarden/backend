package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.Sqresult;
import com.wordgarden.wordgarden.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SqresultRepository extends JpaRepository<Sqresult, Long>{
    List<Sqresult> findByUser(User user);
}
