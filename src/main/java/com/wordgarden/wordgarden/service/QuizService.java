package com.wordgarden.wordgarden.service;

import com.wordgarden.wordgarden.repository.WqinfoRepository;
import org.springframework.stereotype.Service;

@Service
public class QuizService {
    @Autowired
    private WqinfoRepository wqinfoRepository;

    @Autowired
    private WqresultRepository wqresultRepository;

    @Autowired
    private SqinfoRepository sqinfoRepository;

    @Autowired
    private SqresultRepository sqresultRepository;

    @Autowired
    private FriendRepository friendRepository;

    // 자체 퀴즈 목록 조회
    public List<QuizResponse> getOwnQuizzes() {
        return wqinfoRepository.findAll().stream()
                .map(this::convertToQuizResponse)
                .collect(Collectors.toList());
    }

    // 자체 퀴즈 풀기
    public QuizResponse playOwnQuiz(Long quizId, QuizRequest quizRequest) {
        Wqinfo quiz = wqinfoRepository.findById(quizId).orElseThrow();
        return convertToQuizResponse(quiz);
    }

    // 자체 퀴즈 결과 저장
    public void saveOwnQuizResult(Long quizId, QuizRequest quizRequest) {
        Wqresult result = new Wqresult();
        result.setQuizId(quizId);
        result.setScore(quizRequest.getScore());
        result.setUid(quizRequest.getUid());
        wqresultRepository.save(result);
    }

    // 커스텀 퀴즈 생성
    public void createCustomQuiz(QuizRequest quizRequest) {
        Sqinfo quiz = new Sqinfo();
        quiz.setTitle(quizRequest.getTitle());
        quiz.setQuestions(quizRequest.getQuestions());
        sqinfoRepository.save(quiz);
    }

    // 친구와 커스텀 퀴즈 공유
    public void shareCustomQuiz(Long quizId, String friendId) {
        // 공유 기능 로직 추가 (예: 퀴즈 공유 테이블에 저장)
        Friend friend = friendRepository.findById(friendId).orElseThrow();
        Sqinfo quiz = sqinfoRepository.findById(quizId).orElseThrow();
        // Assume there is a method to add shared quizzes to a friend
        friend.addSharedQuiz(quiz);
        friendRepository.save(friend);
    }

    // 공유 받은 퀴즈 목록 조회
    public List<QuizResponse> getSharedQuizzes() {
        // Assume there is a method to get shared quizzes for the current user
        List<Sqinfo> sharedQuizzes = getCurrentUser().getSharedQuizzes();
        return sharedQuizzes.stream()
                .map(this::convertToQuizResponse)
                .collect(Collectors.toList());
    }

    // 공유 받은 퀴즈 결과 저장
    public void saveSharedQuizResult(Long quizId, QuizRequest quizRequest) {
        Sqresult result = new Sqresult();
        result.setQuizId(quizId);
        result.setScore(quizRequest.getScore());
        result.setUid(quizRequest.getUid());
        sqresultRepository.save(result);
    }

    private QuizResponse convertToQuizResponse(Wqinfo quiz) {
        QuizResponse response = new QuizResponse();
        response.setId(quiz.getId());
        response.setTitle(quiz.getTitle());
        response.setQuestions(quiz.getQuestions());
        return response;
    }

    private QuizResponse convertToQuizResponse(Sqinfo quiz) {
        QuizResponse response = new QuizResponse();
        response.setId(quiz.getId());
        response.setTitle(quiz.getTitle());
        response.setQuestions(quiz.getQuestions());
        return response;
    }

    private User getCurrentUser() {
        // Implement logic to get the current logged-in user
        return new User();
    }
}
