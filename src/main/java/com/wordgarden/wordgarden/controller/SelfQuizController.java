package com.wordgarden.wordgarden.controller;

import com.wordgarden.wordgarden.dto.QuestionDTO;
import com.wordgarden.wordgarden.dto.SolveQuizDTO;
import com.wordgarden.wordgarden.dto.SqDTO;
import com.wordgarden.wordgarden.service.SelfQuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sq")
public class SelfQuizController {

    private final SelfQuizService selfQuizService;

    @Autowired
    public SelfQuizController(SelfQuizService selfQuizService){
        this.selfQuizService = selfQuizService;
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createCustomQuiz(@RequestBody SqDTO sqDTO) {
        try {
            List<Long> quizIds = selfQuizService.createCustomQuiz(sqDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("quizTitle", sqDTO.getQuizTitle());
            response.put("quizIds", quizIds);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create quiz: " + e.getMessage());
        }
    }

    @GetMapping("/created/{uid}")
    public ResponseEntity<Object> getCreatedQuizTitlesByUser(@PathVariable String uid) {
        try {
            List<String> quizTitles = selfQuizService.getCreatedQuizTitlesByUser(uid);
            return ResponseEntity.ok(quizTitles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve quiz titles: " + e.getMessage());
        }
    }

    @GetMapping("/created/{uid}/{title}")
    public ResponseEntity<Object> getQuizByUserAndTitle(@PathVariable String uid, @PathVariable String title) {
        try {
            SqDTO quiz = selfQuizService.getQuizByUserAndTitle(uid, title);
            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve quiz: " + e.getMessage());
        }
    }

    @GetMapping("/quiz/{title}")
    public ResponseEntity<Object> getQuizQuestions(@PathVariable String title) {
        try {
            List<QuestionDTO> questions = selfQuizService.getQuizQuestions(title);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve quiz questions: " + e.getMessage());
        }
    }

    @PostMapping("/solve")
    public ResponseEntity<Object> solveQuiz(@RequestBody SolveQuizDTO solveQuizDTO) {
        try {
            selfQuizService.solveQuiz(solveQuizDTO);
            Map<String, String> response = new HashMap<>();
            response.put("message", "제출에 성공하였습니다");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to solve quiz: " + e.getMessage());
        }
    }

    @GetMapping("/solved/{uid}")
    public ResponseEntity<Object> getSolvedQuizTitlesByUser(@PathVariable String uid) {
        try {
            List<String> quizTitles = selfQuizService.getSolvedQuizTitlesByUser(uid);
            return ResponseEntity.ok(quizTitles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve solved quiz titles: " + e.getMessage());
        }
    }

    @GetMapping("/solved/{uid}/{title}")
    public ResponseEntity<Object> getSolvedQuizByUserAndTitle(@PathVariable String uid, @PathVariable String title) {
        try {
            SqDTO quiz = selfQuizService.getSolvedQuizByUserAndTitle(uid, title);
            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve solved quiz: " + e.getMessage());
        }
    }

}