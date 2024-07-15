package com.wordgarden.wordgarden.controller;

import com.wordgarden.wordgarden.entity.Like;
import com.wordgarden.wordgarden.entity.Word;
import com.wordgarden.wordgarden.repository.LikeRepository;
import com.wordgarden.wordgarden.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("like")
public class LikeController {
    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private WordRepository wordRepository;

    // 좋아요 누르기
    @PostMapping("/{wordId}")
    public ResponseEntity<?> toggleLike(@PathVariable String wordId, @RequestParam String userId) {
        Optional<Word> optionalWord = wordRepository.findById(wordId);
        if (!optionalWord.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Word word = optionalWord.get();

        // 사용자가 이미 해당 단어에 좋아요를 눌렀는지 확인
        Like existingLike = likeRepository.findByWordIdAndUserId(wordId, userId);
        if (existingLike != null) {
            // 이미 좋아요를 눌렀으면 삭제
            likeRepository.delete(existingLike);
            return ResponseEntity.ok().build();
        } else {
            // 좋아요를 누르지 않았으면 추가
            Like newLike = new Like();
            newLike.setWord(word);
            newLike.setUserId(userId);
            likeRepository.save(newLike);
            return ResponseEntity.ok().build();
        }
    }

    // 사용자의 좋아요 리스트
    @GetMapping("/{userId}")
    public ResponseEntity<List<Like>> getLikesByUser(@PathVariable String userId) {
        List<Like> likes = likeRepository.findByUserId(userId);
        return ResponseEntity.ok(likes);
    }
}
