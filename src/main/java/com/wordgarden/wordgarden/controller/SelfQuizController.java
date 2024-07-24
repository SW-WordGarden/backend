package com.wordgarden.wordgarden.controller;

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

}