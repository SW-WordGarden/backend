package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.Alarm;
import com.wordgarden.wordgarden.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface AlarmRepository extends JpaRepository<Alarm,Long> {
    List<Alarm> findByToUserOrderByCreateTimeDesc(User toUser);
}
