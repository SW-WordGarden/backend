package com.wordgarden.wordgarden.repository;
import com.wordgarden.wordgarden.entity.Sq;
import com.wordgarden.wordgarden.entity.Sqinfo;
import com.wordgarden.wordgarden.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SqinfoRepository extends JpaRepository<Sqinfo, Long> {
    List<Sqinfo> findByUser(User user);
}
