package com.wordgarden.wordgarden.controller;

import com.wordgarden.wordgarden.dto.QuestionDTO;
import com.wordgarden.wordgarden.dto.SolveQuizDTO;
import com.wordgarden.wordgarden.dto.SqDTO;
import com.wordgarden.wordgarden.service.SelfQuizService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<Map<String, Object>> createCustomQuiz(@RequestBody SqDTO sqDTO) {
        List<Long> quizIds = selfQuizService.createCustomQuiz(sqDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("quizTitle", sqDTO.getQuizTitle());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/created/{uid}")
    public ResponseEntity<List<String>> getCreatedQuizTitlesByUser(@PathVariable String uid) {
        List<String> quizTitles = selfQuizService.getCreatedQuizTitlesByUser(uid);
        return ResponseEntity.ok(quizTitles);
    }

    @GetMapping("/created/{uid}/{title}")
    public ResponseEntity<SqDTO> getQuizByUserAndTitle(@PathVariable String uid, @PathVariable String title) {
        SqDTO quiz = selfQuizService.getQuizByUserAndTitle(uid, title);
        return ResponseEntity.ok(quiz);
    }

    @GetMapping("/quiz/{title}")
    public ResponseEntity<List<QuestionDTO>> getQuizQuestions(@PathVariable String title) {
        List<QuestionDTO> questions = selfQuizService.getQuizQuestions(title);
        return ResponseEntity.ok(questions);
    }

    @PostMapping("/solve")
    public ResponseEntity<Map<String, String>> solveQuiz(@RequestBody SolveQuizDTO solveQuizDTO) {
        selfQuizService.solveQuiz(solveQuizDTO);
        Map<String, String> response = new HashMap<>();
        response.put("message", "제출에 성공하였습니다");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/solved/{uid}")
    public ResponseEntity<List<String>> getSolvedQuizTitlesByUser(@PathVariable String uid) {
        List<String> quizTitles = selfQuizService.getSolvedQuizTitlesByUser(uid);
        return ResponseEntity.ok(quizTitles);
    }

    @GetMapping("/solved/{uid}/{title}")
    public ResponseEntity<SqDTO> getSolvedQuizByUserAndTitle(@PathVariable String uid, @PathVariable String title) {
        SqDTO quiz = selfQuizService.getSolvedQuizByUserAndTitle(uid, title);
        return ResponseEntity.ok(quiz);
    }

}