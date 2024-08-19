package com.wordgarden.wordgarden.controller;

import com.wordgarden.wordgarden.dto.WqResponseDto;
import com.wordgarden.wordgarden.dto.WqSubmissionDto;
import com.wordgarden.wordgarden.dto.WrongAnswerDto;
import com.wordgarden.wordgarden.entity.Wqinfo;
import com.wordgarden.wordgarden.service.WqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/wq")
public class WqController {

    private static final Logger log = LoggerFactory.getLogger(WqController.class);

    private final WqService wqService;

    @Autowired
    public WqController(WqService wqService) {
        this.wqService = wqService;
    }

    // 문제 생성
    @PostMapping("/generate")
    public ResponseEntity<?> generateQuiz() {
        try {
            log.info("Generating new quiz");
            List<WqResponseDto> newQuizQuestions = wqService.generateAndSaveNewQuiz();
            return ResponseEntity.ok(newQuizQuestions);
        } catch (Exception e) {
            log.error("Error generating quiz", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to generate quiz: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 문제 제출
    @PostMapping("/submit")
    public ResponseEntity<Map<String, String>> submitAnswers(@RequestBody WqSubmissionDto submission) {
        try {
            log.info("Received submission for user: {}", submission.getUid());
            wqService.saveResults(submission);

            Map<String, String> response = new HashMap<>();
            response.put("message", "submit ok");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error submitting answers", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to submit answers: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 사용자 별 틀린 문제
    @GetMapping("/wrong/{userId}")
    public ResponseEntity<?> getWrongAnswers(@PathVariable String userId) {
        try {
            log.info("Fetching wrong answers for user: {}", userId);
            List<WrongAnswerDto> wrongAnswers = wqService.getWrongAnswers(userId);
            return ResponseEntity.ok(wrongAnswers);
        } catch (Exception e) {
            log.error("Error fetching wrong answers for user: {}", userId, e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to fetch wrong answers: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 사용자가 푼 퀴즈 제목 찾기
    @GetMapping("/title/{userId}")
    public ResponseEntity<?> getQuizTitlesByUserId(@PathVariable String userId) {
        try {
            Set<String> quizTitles = wqService.getQuizTitlesByUserId(userId);
            return ResponseEntity.ok(quizTitles);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "퀴즈 제목을 불러오는 데 실패했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 제목으로 모든 문제 반환
    @GetMapping("/{wqTitle}")
    public ResponseEntity<?> getQuizByTitle(@PathVariable String wqTitle, @RequestParam String userId) {
        try {
            String decodedTitle = URLDecoder.decode(wqTitle, StandardCharsets.UTF_8.name());
            log.info("Fetching quiz for title: {} and user: {}", decodedTitle, userId);
            List<WqResponseDto> quizQuestions = wqService.getQuizByTitleWithUserAnswers(decodedTitle, userId);
            if (quizQuestions.isEmpty()) {
                log.warn("No questions found for quiz title: {}", decodedTitle);
                return ResponseEntity.notFound().build();
            }
            log.info("Returning {} questions for quiz title: {}", quizQuestions.size(), decodedTitle);
            return ResponseEntity.ok(quizQuestions);
        } catch (Exception e) {
            log.error("Error fetching quiz: ", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "퀴즈를 불러오는 데 실패했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/stats/{userId}")
    public ResponseEntity<?> getUserQuizStats(@PathVariable String userId) {
        try {
            log.info("Fetching quiz stats for user: {}", userId);
            Map<String, Long> stats = wqService.getUserQuizStats(userId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error fetching quiz stats for user: {}", userId, e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "퀴즈 통계를 불러오는 데 실패했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }



}