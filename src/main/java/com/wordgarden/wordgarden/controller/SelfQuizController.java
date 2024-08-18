package com.wordgarden.wordgarden.controller;

import com.wordgarden.wordgarden.dto.QuestionDTO;
import com.wordgarden.wordgarden.dto.SolveQuizDTO;
import com.wordgarden.wordgarden.dto.SqCreatorInfoDto;
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
            String sqId = selfQuizService.createCustomQuiz(sqDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("sqId", sqId);
            response.put("quizTitle", sqDTO.getQuizTitle());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create quiz: " + e.getMessage());
        }
    }

    @GetMapping("/created/{uid}")
    public ResponseEntity<Object> getCreatedQuizTitlesByUser(@PathVariable String uid) {
        try {
            List<Map<String, String>> quizInfo = selfQuizService.getCreatedQuizInfoByUser(uid);
            return ResponseEntity.ok(quizInfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve quiz info: " + e.getMessage());
        }
    }

    @GetMapping("/created/{uid}/{sqId}")
    public ResponseEntity<Object> getQuizByUserAndSqId(@PathVariable String uid, @PathVariable String sqId) {
        try {
            SqDTO quiz = selfQuizService.getQuizByUserAndSqId(uid, sqId);
            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve quiz: " + e.getMessage());
        }
    }

    @GetMapping("/quiz/{sqId}")
    public ResponseEntity<Object> getQuizQuestions(@PathVariable String sqId) {
        try {
            List<QuestionDTO> questions = selfQuizService.getQuizQuestions(sqId);
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

    @GetMapping("/solved/{uid}/{sqId}")
    public ResponseEntity<Object> getSolvedQuizByUserAndSqId(@PathVariable String uid, @PathVariable String sqId) {
        try {
            SqDTO quiz = selfQuizService.getSolvedQuizByUserAndSqId(uid, sqId);
            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve solved quiz: " + e.getMessage());
        }
    }

    // 커스텀 퀴즈 생성자 반환
    @GetMapping("/creator/{sqId}")
    public ResponseEntity<SqCreatorInfoDto> getQuizCreatorInfo(@PathVariable String sqId) {
        try {
            SqCreatorInfoDto creatorInfo = selfQuizService.getQuizCreatorInfo(sqId);
            return ResponseEntity.ok(creatorInfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

}