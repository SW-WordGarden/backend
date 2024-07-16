package com.wordgarden.wordgarden.controller;

import com.wordgarden.wordgarden.entity.Like;
import com.wordgarden.wordgarden.entity.User;
import com.wordgarden.wordgarden.entity.Word;
import com.wordgarden.wordgarden.repository.LikeRepository;
import com.wordgarden.wordgarden.repository.WordRepository;
import com.wordgarden.wordgarden.repository.UserRepository;
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

    // 좋아요 토글
    @PostMapping("/{uid}/{wordId}")
    public ResponseEntity<?> toggleLike(@PathVariable String uid, @PathVariable String wordId) {
        Optional<Word> optionalWord = wordRepository.findById(wordId);
        Optional<User> optionalUser = userRepository.findById(uid);

        if (!optionalWord.isPresent() || !optionalUser.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Word word = optionalWord.get();
        User user = optionalUser.get();

        // 사용자가 이미 해당 단어에 좋아요를 눌렀는지 확인
        Like existingLike = likeRepository.findByUserAndWord(user, word);

        if (existingLike != null) {
            // 이미 좋아요를 눌렀으면 삭제
            likeRepository.delete(existingLike);
            return ResponseEntity.ok().build();
        } else {
            // 좋아요를 누르지 않았으면 추가
            Like newLike = new Like();
            newLike.setWord(word);
            newLike.setUser(user);
            likeRepository.save(newLike);
            return ResponseEntity.ok().build();
        }
    }

//    // 사용자의 좋아요 리스트 조회
//    @GetMapping("/{uid}")
//    public ResponseEntity<List<Word>> getLikesByUser(@PathVariable String uid) {
//        Optional<User> optionalUser = userRepository.findByUid(uid);
//
//        if (!optionalUser.isPresent()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        User user = optionalUser.get();
//        List<Like> likes = likeRepository.findByUser(user);
//        List<Word> likedWords = likes.stream().map(Like::getWord).collect(Collectors.toList());
//
//        return ResponseEntity.ok(likedWords);
//    }
}
