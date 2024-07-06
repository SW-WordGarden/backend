package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, String> {
}