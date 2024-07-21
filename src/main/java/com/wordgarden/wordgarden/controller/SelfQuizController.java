package com.wordgarden.wordgarden.controller;

import com.wordgarden.wordgarden.dto.SqDTO;
import com.wordgarden.wordgarden.service.SelfQuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        List<Long> quizIds = selfQuizService.createCustomQuiz(sqDTO);  // 여기를 수정했습니다.

        Map<String, Object> response = new HashMap<>();
        response.put("quizTitle", sqDTO.getQuizTitle());

        return ResponseEntity.ok(response);
    }
}