package com.wordgarden.wordgarden.controller;

import com.wordgarden.wordgarden.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quiz")

public class QuizController {
    @Autowired
    private QuizService quizService;

    // 자체 퀴즈 목록 조회
    @GetMapping("/wq")
    public List<QuizResponse> getOwnQuizzes() {
        return quizService.getOwnQuizzes();
    }

    // 자체 퀴즈 풀기
    @PostMapping("/wq/{quizId}/play")
    public QuizResponse playOwnQuiz(@PathVariable Long quizId, @RequestBody QuizRequest quizRequest) {
        return quizService.playOwnQuiz(quizId, quizRequest);
    }

    // 자체 퀴즈 결과 저장
    @PostMapping("/wq/{quizId}/result")
    public void saveOwnQuizResult(@PathVariable Long quizId, @RequestBody QuizRequest quizRequest) {
        quizService.saveOwnQuizResult(quizId, quizRequest);
    }

    // 커스텀 퀴즈 생성
    @PostMapping("/sq")
    public void createCustomQuiz(@RequestBody QuizRequest quizRequest) {
        quizService.createCustomQuiz(quizRequest);
    }

    // 친구와 커스텀 퀴즈 공유
    @PostMapping("/sq/{quizId}/share")
    public void shareCustomQuiz(@PathVariable Long quizId, @RequestBody String friendId) {
        quizService.shareCustomQuiz(quizId, friendId);
    }

    // 공유 받은 퀴즈 목록 조회
    @GetMapping("/shared")
    public List<QuizResponse> getSharedQuizzes() {
        return quizService.getSharedQuizzes();
    }

    // 공유 받은 퀴즈 결과 저장
    @PostMapping("/shared/{quizId}/result")
    public void saveSharedQuizResult(@PathVariable Long quizId, @RequestBody QuizRequest quizRequest) {
        quizService.saveSharedQuizResult(quizId, quizRequest);
    }
}
