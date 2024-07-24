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
    public ResponseEntity<List<SqDTO>> getCreatedQuizzesByUser(@PathVariable String uid) {
        List<SqDTO> quizzes = selfQuizService.getCreatedQuizzesByUser(uid);
        return ResponseEntity.ok(quizzes);
    }


}