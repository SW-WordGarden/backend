package com.wordgarden.wordgarden.controller;

import com.wordgarden.wordgarden.entity.Word;
import com.wordgarden.wordgarden.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
단어 관련 컨트롤러

 */

@RestController
@RequestMapping("/word")
public class WordController {
    @Autowired
    private WordService wordService;

    // 단어등록
    @PostMapping("/load")
    public ResponseEntity<String> loadWordsFromCSV() {
        wordService.loadWordsFromCSV();
        return ResponseEntity.ok("Words loaded successfully from CSV");
    }

    // 카테고리별 단어로 주간 학습 리스트 구성
    @GetMapping("/weekly/{category}")
    public ResponseEntity<List<Word>> getWeeklyWords(@PathVariable String category) {
        List<Word> words = wordService.getWeeklyWordsByCategory(category);
        return ResponseEntity.ok(words);
    }

    // 단어정보 가져오기.
    @GetMapping("/{word}")
    public ResponseEntity<Word> getWordInfo(@PathVariable String word) {
        Word wordInfo = wordService.getWordInfo(word);
        return ResponseEntity.ok(wordInfo);
    }

    // 좋아요 단어 저장
    @PostMapping("/like/{wordId}")
    public ResponseEntity<String> likeWord(@PathVariable String wordId, @RequestParam String uid) {
        wordService.likeWord(wordId, uid);
        return ResponseEntity.ok("Word liked successfully");
    }

}
