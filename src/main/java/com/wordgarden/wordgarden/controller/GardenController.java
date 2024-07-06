package com.wordgarden.wordgarden.controller;

import com.wordgarden.wordgarden.service.GardenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/garden")
public class GardenController {

    @Autowired
    private GardenService gardenService;

    // 정원 상태 조회
    @GetMapping("/{uid}")
    public ResponseEntity<GardenStatus> getGardenStatus(@PathVariable String uid) {
        GardenStatus gardenStatus = gardenService.getGardenStatus(uid);
        return ResponseEntity.ok(gardenStatus);
    }

    // 나무 성장 단계 업데이트
    @PutMapping("/{uid}/grow")
    public ResponseEntity<String> updateTreeGrowth(@PathVariable String uid, @RequestBody int quizScore) {
        gardenService.updateTreeGrowth(uid, quizScore);
        return ResponseEntity.ok("Tree growth updated successfully");
    }

    // 도감 조회
    @GetMapping("/{uid}/gardenbook")
    public ResponseEntity<List<GardenStatus>> getGardenBook(@PathVariable String uid) {
        List<GardenStatus> gardenBook = gardenService.getGardenBook(uid);
        return ResponseEntity.ok(gardenBook);
    }
}
