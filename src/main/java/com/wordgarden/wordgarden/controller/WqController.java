package com.wordgarden.wordgarden.controller;

import com.wordgarden.wordgarden.dto.WqResponseDto;
import com.wordgarden.wordgarden.dto.WqSubmissionDto;
import com.wordgarden.wordgarden.dto.WrongAnswerDto;
import com.wordgarden.wordgarden.service.WqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/wq")
public class WqController {

    private static final Logger log = LoggerFactory.getLogger(WqController.class);

    private final WqService wqService;

    @Autowired
    public WqController(WqService wqService) {
        this.wqService = wqService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateQuiz() {
        try {
            log.info("Generating new quiz");
            List<WqResponseDto> quiz = wqService.generateQuiz();
            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            log.error("Error generating quiz", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to generate quiz: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

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
}