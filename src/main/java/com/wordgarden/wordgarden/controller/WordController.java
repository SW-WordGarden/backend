package com.wordgarden.wordgarden.controller;

import com.wordgarden.wordgarden.dto.LearningDTO;
import com.wordgarden.wordgarden.dto.WordDTO;
import com.wordgarden.wordgarden.entity.Word;
import com.wordgarden.wordgarden.repository.WordRepository;
import com.wordgarden.wordgarden.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @GetMapping("/{wordId}")
    public ResponseEntity<Word> getWordById(@PathVariable String wordId) {
        Word word = wordRepository.findByWordId(wordId);
        if (word != null) {
            return ResponseEntity.ok(word);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 특정 카테고리의 모든 단어들 조회
    @GetMapping("/{category}")
    public ResponseEntity<List<WordDTO>> getWordsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(wordService.getWordsByCategory(category));
    }

    // 이번주 학습 단어들 전부 조회
    @GetMapping("/weekly")
    public ResponseEntity<List<LearningDTO>> getLearningWords() {
        return ResponseEntity.ok(wordService.getLearningWords());
    }

    // 이번주 학습 단어들 카테고리별 조회
    @GetMapping("/weekly/{category}")
    public ResponseEntity<List<LearningDTO>> getLearningWordsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(wordService.getLearningWordsByCategory(category));
    }

    // 자동으로 이루어짐 - 7일 주기로 단어 자동으로 바뀌는 부분
    @PostMapping("/update-learning")
    public ResponseEntity<Void> updateLearningWords() {
        wordService.updateLearningWords();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/clean-weekly")
    public ResponseEntity<Void> cleanUpWeeklyWords() {
        wordService.cleanUpWeeklyWords();
        return ResponseEntity.ok().build();
    }

}
