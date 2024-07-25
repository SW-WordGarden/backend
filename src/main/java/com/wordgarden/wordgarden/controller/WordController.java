package com.wordgarden.wordgarden.controller;

import com.wordgarden.wordgarden.dto.LearningDTO;
import com.wordgarden.wordgarden.dto.WordDTO;
import com.wordgarden.wordgarden.entity.Word;
import com.wordgarden.wordgarden.repository.WordRepository;
import com.wordgarden.wordgarden.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/word")
public class WordController {

    @Autowired
    private WordService wordService;
    @Autowired
    private WordRepository wordRepository;

    // csv파일에서 단어 로드 후 데이터베이스 저장 -- 서버 구동 동시에 작동되게끔
    @PostMapping("/load")
    public void loadWordsFromCSV(){
        wordService.loadWordsFromCSV();
    }

    // 특정 단어 조회
    @GetMapping("words/{wordId}")
    public ResponseEntity<Object> getWordById(@PathVariable String wordId) {
        Word word = wordRepository.findByWordId(wordId);
        if (word != null) {
            return ResponseEntity.ok(word);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Word not found");
        }
    }

//    // 카테고리별 단어 조회
//    @GetMapping("/category/{category}")
//    public ResponseEntity<List<WordDTO>> getWordsByCategory(@PathVariable String category) {
//        return ResponseEntity.ok(wordService.getWordsByCategory(category));
//    }

    // Learning 단어 조회
    @GetMapping("/learning")
    public ResponseEntity<Object> getLearningWords() {
        try {
            List<LearningDTO> learningWords = wordService.getLearningWords();
            return ResponseEntity.ok(learningWords);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve learning words: " + e.getMessage());
        }
    }

    // 카테고리별 Learning 단어 조회
    @GetMapping("/learning/{category}")
    public ResponseEntity<Object> getLearningWordsByCategory(@PathVariable String category) {
        try {
            List<LearningDTO> learningWords = wordService.getLearningWordsByCategory(category);
            return ResponseEntity.ok(learningWords);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve learning words for category: " + e.getMessage());
        }
    }

    // 매주 7일마다 Learning 업데이트 요청
    @PostMapping("/update-learning")
    public ResponseEntity<Object> updateLearningWords() {
        try {
            wordService.updateLearningWords();
            return ResponseEntity.ok("Learning words updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update learning words: " + e.getMessage());
        }
    }

    // Weekly 초기화 요청
    @PostMapping("/clean-weekly")
    public ResponseEntity<Object> cleanUpWeeklyWords() {
        try {
            wordService.cleanUpWeeklyWords();
            return ResponseEntity.ok("Weekly words cleaned up successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to clean up weekly words: " + e.getMessage());
        }
    }

}
