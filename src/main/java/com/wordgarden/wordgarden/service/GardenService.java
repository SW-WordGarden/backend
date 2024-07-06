package com.wordgarden.wordgarden.service;

import org.springframework.stereotype.Service;

@Service
public class GardenService {
    @Autowired
    private GardenRepository gardenRepository;

    @Autowired
    private UserRepository userRepository;

    public GardenStatus getGardenStatus(String uid) {
        Garden garden = gardenRepository.findByUid(uid).orElseThrow(() -> new IllegalArgumentException("Garden not found"));
        return new GardenStatus(garden);
    }

    public void updateTreeGrowth(String uid, int quizScore) {
        Garden garden = gardenRepository.findByUid(uid).orElseThrow(() -> new IllegalArgumentException("Garden not found"));

        // 점수에 따라 나무 성장 로직 구현
        int currentGrowth = garden.getTreeGrow();
        int newGrowth = currentGrowth + quizScore; // 간단한 예로 점수만큼 성장
        garden.setTreeGrow(newGrowth);

        // 성장 단계 업데이트
        if (newGrowth >= 40) {
            garden.setTreeStage("꽃이 핀 큰 나무");
        } else if (newGrowth >= 30) {
            garden.setTreeStage("작은 나무");
        } else if (newGrowth >= 20) {
            garden.setTreeStage("새싹");
        } else {
            garden.setTreeStage("씨앗");
        }

        gardenRepository.save(garden);

        // 도감에 등록
        if (garden.getTreeStage().equals("꽃이 핀 큰 나무")) {
            registerInGardenBook(garden);
        }
    }

    private void registerInGardenBook(Garden garden) {
        // 도감 등록 로직 구현
        // GardenBook 엔티티에 등록된 나무 정보 저장
        // 예시
        // GardenBook gardenBook = new GardenBook();
        // gardenBook.setTreeName(garden.getTreeName());
        // gardenBook.setTreeResult(garden.getTreeResult());
        // gardenBook.setGardenId(garden.getGardenId());
        // gardenBookRepository.save(gardenBook);
    }

    public List<GardenStatus> getGardenBook(String uid) {
        return gardenRepository.findGardenBookByUid(uid);
    }
}
