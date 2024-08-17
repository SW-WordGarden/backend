package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm,Long> {
}
