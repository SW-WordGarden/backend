package com.wordgarden.wordgarden.service;

import com.wordgarden.wordgarden.entity.Garden;
import com.wordgarden.wordgarden.entity.GardenBook;
import com.wordgarden.wordgarden.entity.User;
import com.wordgarden.wordgarden.repository.GardenRepository;
import com.wordgarden.wordgarden.repository.GardenBookRepository;
import com.wordgarden.wordgarden.repository.UserRepository;
import com.wordgarden.wordgarden.dto.UserGardenInfoDTO;
import com.wordgarden.wordgarden.dto.PlantGrowthDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class GardenService {

    private final GardenRepository gardenRepository;
    private final GardenBookRepository gardenBookRepository;
    private final UserRepository userRepository;

    @Autowired
    public GardenService(GardenRepository gardenRepository, GardenBookRepository gardenBookRepository, UserRepository userRepository) {
        this.gardenRepository = gardenRepository;
        this.gardenBookRepository = gardenBookRepository;
        this.userRepository = userRepository;
    }

    // 사용자의 정원 정보를 조회하는 메서드
    public UserGardenInfoDTO getUserGardenInfo(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        Garden garden = gardenRepository.findByUser(user).orElseThrow(() -> new RuntimeException("정원을 찾을 수 없습니다"));
        long plantCount = gardenBookRepository.countByGarden(garden);

        int coins = garden.getCoin() != null ? garden.getCoin() : 0;
        int wateringCans = garden.getWater() != null ? garden.getWater() : 0;

        return new UserGardenInfoDTO(coins, wateringCans, plantCount);
    }

    // 현재 키우고 있는 식물의 성장 정보를 조회하는 메서드
    public PlantGrowthDTO getPlantGrowth(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        Garden garden = gardenRepository.findByUser(user).orElseThrow(() -> new RuntimeException("정원을 찾을 수 없습니다"));

        int growthStage = calculateGrowthStage(garden.getTreeGrow());
        boolean isFullyGrown = growthStage == 4;
        LocalDate completionDate = isFullyGrown ? LocalDate.now() : null;

        return new PlantGrowthDTO(garden.getTreeName(), garden.getTreeGrow(), growthStage, completionDate);
    }

    // 물뿌리개를 구매하는 메서드
    @Transactional
    public boolean buyWateringCan(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        Garden garden = gardenRepository.findByUser(user).orElseThrow(() -> new RuntimeException("정원을 찾을 수 없습니다"));

        if (garden.getCoin() >= 1000) {
            garden.setCoin(garden.getCoin() - 1000);
            garden.setWater(garden.getWater() + 10);
            gardenRepository.save(garden);
            return true;
        }
        return false;
    }

    // 식물의 성장 단계를 계산하는 메서드
    private int calculateGrowthStage(int treeGrow) {
        if (treeGrow <= 1000) return 1;
        if (treeGrow <= 2250) return 2;
        if (treeGrow <= 3749) return 3;
        return 4;
    }

    // 정원 도감을 업데이트하고 새로운 식물을 설정하는 메서드
    @Transactional
    public void updateGardenBook(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        Garden garden = gardenRepository.findByUser(user).orElseThrow(() -> new RuntimeException("정원을 찾을 수 없습니다"));

        if (calculateGrowthStage(garden.getTreeGrow()) == 4) {
            // 완성된 식물을 정원 도감에 추가
            GardenBook gardenBook = new GardenBook();
            gardenBook.setGarden(garden);
            gardenBook.setTreeName(garden.getTreeName());
            gardenBook.setTreeResult(garden.getTree());
            gardenBookRepository.save(gardenBook);

            // 다음 식물로 정원을 초기화
            List<String> plants = List.of("Apple Tree", "Cherry Blossom", "Tulip", "Sunflower");
            int nextPlantIndex = plants.indexOf(garden.getTreeName()) + 1;
            if (nextPlantIndex < plants.size()) {
                garden.setTreeName(plants.get(nextPlantIndex));
                garden.setTreeGrow(0);
                gardenRepository.save(garden);
            }
        }
    }

}