package com.wordgarden.wordgarden.repository;
import com.wordgarden.wordgarden.entity.Sqinfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SqinfoRepository extends JpaRepository<Sqinfo, Long> {
}
