package com.wordgarden.wordgarden.service;

import com.wordgarden.wordgarden.dto.*;
import com.wordgarden.wordgarden.entity.Sq;
import com.wordgarden.wordgarden.entity.Sqinfo;
import com.wordgarden.wordgarden.entity.Sqresult;
import com.wordgarden.wordgarden.entity.User;
import com.wordgarden.wordgarden.repository.SqRepository;
import com.wordgarden.wordgarden.repository.SqinfoRepository;
import com.wordgarden.wordgarden.repository.SqresultRepository;
import com.wordgarden.wordgarden.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

@Service
public class SelfQuizService {

    private static final Logger log = LoggerFactory.getLogger(SelfQuizService.class);

    @Autowired
    private SqinfoRepository sqinfoRepository;
    @Autowired
    private SqRepository sqRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SqresultRepository sqresultRepository;
    @Autowired
    private GardenService gardenService;

    // 문제 생성
    @Transactional
    public String createCustomQuiz(SqDTO sqDTO) {
        User user = userRepository.findById(sqDTO.getUid())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (sqinfoRepository.existsByUserAndSqTitle(user, sqDTO.getQuizTitle())) {
            throw new RuntimeException("A quiz with this title already exists for this user");
        }

        long quizCount = sqinfoRepository.count();

        Sqinfo sqinfo = new Sqinfo();
        sqinfo.setUser(user);
        sqinfo.setSqTitle(sqDTO.getQuizTitle());
        sqinfo.generateSqId(quizCount + 1);
        Sqinfo savedSqinfo = sqinfoRepository.save(sqinfo);

        for (int i = 0; i < sqDTO.getQuestionsAndAnswers().size(); i++) {
            QuestionAnswerDTO qaDTO = sqDTO.getQuestionsAndAnswers().get(i);

            Sq sq = new Sq();
            sq.setSqinfo(savedSqinfo);
            sq.setSqQuestion(qaDTO.getQuestion());
            sq.setSqAnswer(qaDTO.getAnswer());
            sq.setSqQnum(i + 1);
            sq.setSqTitle(sqDTO.getQuizTitle());
            sqRepository.save(sq);
        }

        return savedSqinfo.getSqId();
    }


    // 생성한 퀴즈 반환
    public List<String> getCreatedQuizTitlesByUser(String uid) {
        return sqinfoRepository.findTitlesByUserUid(uid);
    }

    public SqDTO getQuizByUserAndTitle(String uid, String title) {
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Sqinfo sqinfo = sqinfoRepository.findByUserAndSqTitle(user, title)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        List<Sq> sqs = sqRepository.findBySqinfoOrderBySqQnumAsc(sqinfo);

        SqDTO sqDTO = new SqDTO();
        sqDTO.setUid(uid);
        sqDTO.setQuizTitle(title);
        sqDTO.setQuestionsAndAnswers(new ArrayList<>());

        for (Sq sq : sqs) {
            QuestionAnswerDTO qaDTO = new QuestionAnswerDTO();
            qaDTO.setQuestion(sq.getSqQuestion());
            qaDTO.setAnswer(sq.getSqAnswer());
            qaDTO.setSqQnum(sq.getSqQnum());
            sqDTO.getQuestionsAndAnswers().add(qaDTO);
        }

        return sqDTO;
    }

    // 퀴즈 관련 문제만 보이기
    public List<QuestionDTO> getQuizQuestions(String sqId) {
        Sqinfo sqinfo = sqinfoRepository.findById(sqId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        List<Sq> sqs = sqRepository.findBySqinfoOrderBySqQnumAsc(sqinfo);

        return sqs.stream()
                .map(sq -> new QuestionDTO(sq.getId(), sq.getSqQuestion(), sq.getSqQnum()))
                .collect(Collectors.toList());
    }


    // 입력 받은 답 처리
    @Transactional
    public void solveQuiz(SolveQuizDTO solveQuizDTO) {
        User user = userRepository.findById(solveQuizDTO.getUid())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Sqinfo sqinfo = sqinfoRepository.findBySqTitle(solveQuizDTO.getQuizTitle())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        int correctAnswers = 0;

        for (AnswerDTO answerDTO : solveQuizDTO.getAnswers()) {
            try {
                Sq sq = sqRepository.findById(answerDTO.getQuestionId())
                        .orElseThrow(() -> new RuntimeException("Question not found"));

                Sqresult sqresult = new Sqresult();
                sqresult.setUser(user);
                sqresult.setSqinfo(sqinfo);
                sqresult.setUSqA(answerDTO.getUserAnswer());
                sqresult.setSqQnum(sq.getSqQnum());
                sqresult.setTime(new Timestamp(System.currentTimeMillis()));

                // 정답 체크
                boolean isCorrect = sq.getSqAnswer().equalsIgnoreCase(answerDTO.getUserAnswer());
                sqresult.setSqCheck(isCorrect);

                if (isCorrect) {
                    correctAnswers++;
                }

                sqresultRepository.save(sqresult);
            } catch (Exception e) {
                log.error("답변 처리 중 오류 발생: {}", answerDTO, e);
            }
        }

        // Point와 Coin 증가
        try {
            int pointsEarned = correctAnswers * 25;
            int currentPoints = user.getUPoint() != null ? user.getUPoint() : 0;
            user.setUPoint(currentPoints + pointsEarned);
            userRepository.save(user);

            gardenService.increaseCoins(user.getUid(), pointsEarned);

            log.info("사용자 {}의 포인트와 코인이 {} 만큼 증가했습니다.", user.getUid(), pointsEarned);
        } catch (Exception e) {
            log.error("포인트 및 코인 증가 중 오류 발생: {}", e.getMessage());
        }
    }

    // 풀이한 답 처리
    public List<String> getSolvedQuizTitlesByUser(String uid) {
        return sqresultRepository.findDistinctSqTitlesByUserUid(uid);
    }

    public SqDTO getSolvedQuizByUserAndTitle(String uid, String title) {
        List<Sqresult> results = sqresultRepository.findByUser_UidAndSqinfo_SqTitle(uid, title);

        if (results.isEmpty()) {
            throw new RuntimeException("No solved quiz found for user " + uid + " and title " + title);
        }

        SqDTO sqDTO = new SqDTO();
        sqDTO.setUid(uid);
        sqDTO.setQuizTitle(title);
        sqDTO.setQuestionsAndAnswers(new ArrayList<>());
        sqDTO.setSqresults(new ArrayList<>());

        for (Sqresult result : results) {
            SqresultDTO sqresultDTO = new SqresultDTO();
            sqresultDTO.setUserAnswer(result.getUSqA());
            sqresultDTO.setCorrect(result.getSqCheck());
            sqresultDTO.setTime(result.getTime());
            sqresultDTO.setSqQnum(result.getSqQnum());
            sqDTO.getSqresults().add(sqresultDTO);
        }

        // 원본 문제와 정답 정보 조회
        List<Sq> questions = sqRepository.findBySqTitle(title);
        for (Sq sq : questions) {
            QuestionAnswerDTO qaDTO = new QuestionAnswerDTO();
            qaDTO.setQuestion(sq.getSqQuestion());
            qaDTO.setAnswer(sq.getSqAnswer());
            qaDTO.setSqQnum(sq.getSqQnum());
            sqDTO.getQuestionsAndAnswers().add(qaDTO);
        }

        return sqDTO;
    }

    public List<Map<String, String>> getCreatedQuizInfoByUser(String uid) {
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Sqinfo> userQuizzes = sqinfoRepository.findByUser(user);

        return userQuizzes.stream()
                .map(quiz -> {
                    Map<String, String> quizInfo = new HashMap<>();
                    quizInfo.put("sqId", quiz.getSqId());
                    quizInfo.put("title", quiz.getSqTitle());
                    return quizInfo;
                })
                .collect(Collectors.toList());
    }


    public SqDTO getQuizByUserAndSqId(String uid, String sqId) {
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Sqinfo sqinfo = sqinfoRepository.findByUserAndSqId(user, sqId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        List<Sq> sqs = sqRepository.findBySqinfoOrderBySqQnumAsc(sqinfo);

        SqDTO sqDTO = new SqDTO();
        sqDTO.setUid(uid);
        sqDTO.setQuizTitle(sqinfo.getSqTitle());
        sqDTO.setSqId(sqinfo.getSqId());
        sqDTO.setQuestionsAndAnswers(new ArrayList<>());

        for (Sq sq : sqs) {
            QuestionAnswerDTO qaDTO = new QuestionAnswerDTO();
            qaDTO.setQuestion(sq.getSqQuestion());
            qaDTO.setAnswer(sq.getSqAnswer());
            qaDTO.setSqQnum(sq.getSqQnum());
            sqDTO.getQuestionsAndAnswers().add(qaDTO);
        }

        return sqDTO;
    }

}
