package com.wordgarden.wordgarden.controller;

import com.wordgarden.wordgarden.service.GardenService;
import com.wordgarden.wordgarden.dto.UserGardenInfoDTO;
import com.wordgarden.wordgarden.dto.PlantGrowthDTO;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/garden")
public class GardenController {

    private final GardenService gardenService;
    private final Bucket bucket;

    @Autowired
    public GardenController(GardenService gardenService) {
        this.gardenService = gardenService;

        // api컨트롤러
        Bandwidth limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
        this.bucket = Bucket.builder()
                .addLimit(limit)
                .build();
    }

    // 사용자의 정원 정보를 조회하는 엔드포인트
    // 코인, 물뿌리개 수, 보유한 식물 수를 반환
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserGardenInfoDTO> getUserGardenInfo(@PathVariable String userId) {
        UserGardenInfoDTO userInfo = gardenService.getUserGardenInfo(userId);
        return ResponseEntity.ok(userInfo);
    }

    // 현재 키우고 있는 식물의 성장 정보를 조회하는 엔드포인트
    // 식물 이름, 성장값, 성장 단계, 완성 날짜(완성 시)를 반환
    @GetMapping("/grow/{userId}")
    public ResponseEntity<PlantGrowthDTO> getPlantGrowth(@PathVariable String userId) {
        PlantGrowthDTO plantGrowth = gardenService.getPlantGrowth(userId);
        return ResponseEntity.ok(plantGrowth);
    }

    // 물뿌리개를 구매하는 엔드포인트
    // 1000코인을 소비하여 물뿌리개 1개를 구매
    @PostMapping("/buy/{userId}")
    public ResponseEntity<String> buyWateringCan(@PathVariable String userId) {
        boolean success = gardenService.buyWateringCan(userId);
        if (success) {
            return ResponseEntity.ok("물뿌리개 구매 성공");
        } else {
            return ResponseEntity.badRequest().body("코인이 부족하여 물뿌리개를 구매할 수 없습니다");
        }
    }

    // 코인 증가 (테스트용)
    @PostMapping("/{userId}/coin")
    public ResponseEntity<String> increaseCoins(@PathVariable String userId, @RequestParam int amount) {
        try {
            gardenService.increaseCoins(userId, amount);
            return ResponseEntity.ok("코인이 성공적으로 증가되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 나무 성장
    @PostMapping("/{userId}/watertree")
    public ResponseEntity<String> useWateringCan(@PathVariable String userId) {
        boolean success = gardenService.useWateringCan(userId);
        if (success) {
            return ResponseEntity.ok("물뿌리개를 사용하여 나무의 성장도가 25 증가했습니다.");
        } else {
            return ResponseEntity.badRequest().body("물뿌리개를 사용할 수 없습니다. 물뿌리개가 부족합니다.");
        }
    }
}