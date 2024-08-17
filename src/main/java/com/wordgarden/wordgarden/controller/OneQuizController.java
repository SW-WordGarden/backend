package com.wordgarden.wordgarden.controller;
import com.wordgarden.wordgarden.dto.WqResponseDto;
import com.wordgarden.wordgarden.service.OneQuizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/onequiz")
public class OneQuizController {
    private static final Logger logger = LoggerFactory.getLogger(OneQuizController.class);
    private final OneQuizService oneQuizService;

    @Autowired
    public OneQuizController(OneQuizService oneQuizService) {
        this.oneQuizService = oneQuizService;
    }

    @GetMapping("/generate/{tableType}")
    public ResponseEntity<?> getQuiz(@PathVariable String tableType) {
        try {
            WqResponseDto quiz = oneQuizService.generateQuiz(tableType);
            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            logger.error("퀴즈 생성 중 오류 발생", e);
            return ResponseEntity.badRequest().body("퀴즈 생성 실패: " + e.getMessage());
        }
    }

    @PostMapping("/submit")
    public ResponseEntity<String> submitQuiz(
            @RequestParam String userId,
            @RequestParam String wqId,
            @RequestParam String userAnswer) {
        try {
            oneQuizService.saveQuizResult(userId, wqId, userAnswer);
            return ResponseEntity.ok("퀴즈 결과가 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            logger.error("퀴즈 결과 저장 중 오류 발생", e);
            return ResponseEntity.badRequest().body("퀴즈 결과 저장에 실패했습니다: " + e.getMessage());
        }
    }
}
