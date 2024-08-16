package com.wordgarden.wordgarden.repository;

import com.wordgarden.wordgarden.entity.Garden;
import com.wordgarden.wordgarden.entity.GardenBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GardenBookRepository extends JpaRepository<GardenBook, Long> {
    // 특정 정원에 속한 식물의 수를 세는 메서드
    long countByGarden(Garden garden);
}