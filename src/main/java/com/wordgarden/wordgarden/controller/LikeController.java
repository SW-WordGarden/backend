package com.wordgarden.wordgarden.controller;

import com.wordgarden.wordgarden.dto.WordDTO;
import com.wordgarden.wordgarden.entity.Like;
import com.wordgarden.wordgarden.entity.User;
import com.wordgarden.wordgarden.entity.Word;
import com.wordgarden.wordgarden.repository.LikeRepository;
import com.wordgarden.wordgarden.repository.WordRepository;
import com.wordgarden.wordgarden.repository.UserRepository;
import com.wordgarden.wordgarden.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/like")
public class LikeController {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WordService wordService;

    // 좋아요 토글
    @PostMapping("/toggle/{uid}/{wordId}")
    public ResponseEntity<Boolean> toggleLike(@PathVariable String uid, @PathVariable String wordId) {
        boolean isLiked = wordService.toggleLike(uid, wordId);
        return ResponseEntity.ok(isLiked);
    }

    // 사용자의 좋아요 리스트 조회
    @GetMapping("/list/{uid}")
    public ResponseEntity<List<WordDTO>> getLikedWords(@PathVariable String uid) {
        List<WordDTO> likedWords = wordService.getLikedWords(uid);
        if (likedWords.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(likedWords);
    }

    // 특정 단어의 좋아요 상태 확인
    @GetMapping("/status/{uid}/{wordId}")
    public ResponseEntity<Boolean> checkLikeStatus(@PathVariable String uid, @PathVariable String wordId) {
        boolean isLiked = wordService.checkLikeStatus(uid, wordId);
        return ResponseEntity.ok(isLiked);
    }
}