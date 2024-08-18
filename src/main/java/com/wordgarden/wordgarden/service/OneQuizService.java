package com.wordgarden.wordgarden.service;

import com.wordgarden.wordgarden.dto.OneQuizCorrectDto;
import com.wordgarden.wordgarden.dto.WqResponseDto;
import com.wordgarden.wordgarden.entity.*;
import com.wordgarden.wordgarden.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
@Service
public class OneQuizService {
    private static final Logger logger = LoggerFactory.getLogger(OneQuizService.class);

    @Autowired
    private WeeklyRepository weeklyRepository;
    @Autowired
    private LearningRepository learningRepository;
    @Autowired
    private WqinfoRepository wqinfoRepository;
    @Autowired
    private WqresultRepository wqresultRepository;
    @Autowired
    private WqwrongRepository wqwrongRepository;
    @Autowired
    private UserRepository userRepository;

    // 잠금화면 퀴즈 생성
    @Transactional
    public WqResponseDto generateQuiz(String tableType, String uid) {

        User user = userRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + uid));

        if (!user.getULockquiz()) {
            throw new RuntimeException("잠금화면 퀴즈가 비활성화되어 있습니다.");
        }

        List<? extends Object> words = getWords(tableType);
        logger.info("{} 단어 수: {}", tableType, words.size());

        if (words.isEmpty()) {
            logger.warn("{} 단어가 없습니다.", tableType);
            throw new RuntimeException(tableType + " 단어가 없습니다.");
        }

        Word word = getRandomWord(words);

        long quizCount = wqinfoRepository.countByWqTitlePrefix("잠금화면 퀴즈_");
        String quizTitle = "잠금화면 퀴즈_" + (quizCount + 1);

        Wqinfo wqinfo = new Wqinfo();
        wqinfo.setWqId("lock_" + (quizCount + 1));
        wqinfo.setWqTitle(quizTitle);
        wqinfo.setWord(word);

        boolean isCorrect = new Random().nextBoolean();
        if (isCorrect) {
            wqinfo.setWqQuestion(word.getWord() + " - " + word.getWordInfo());
            wqinfo.setWqAnswer("O");
        } else {
            Word randomWord = getRandomWordExcept(words, word);
            wqinfo.setWqQuestion(word.getWord() + " - " + randomWord.getWordInfo());
            wqinfo.setWqAnswer("X");
        }

        wqinfo = wqinfoRepository.save(wqinfo);

        WqResponseDto response = new WqResponseDto();
        response.setWqId(wqinfo.getWqId());
        response.setWqTitle(wqinfo.getWqTitle());
        response.setWordId(word.getWordId());
        response.setWord(word.getWord());
        response.setWqQuestion(wqinfo.getWqQuestion());
        response.setQuestionType("ox");
        response.setOptions(Arrays.asList("O", "X"));

        logger.info("생성된 퀴즈: {}", response);
        return response;
    }

    // 단어 랜덤 선택
    private List<? extends Object> getWords(String tableType) {
        if ("weekly".equalsIgnoreCase(tableType)) {
            return weeklyRepository.findAll();
        } else if ("learning".equalsIgnoreCase(tableType)) {
            return learningRepository.findAll();
        }
        throw new IllegalArgumentException("Invalid table type: " + tableType);
    }
    private Word getRandomWord(List<? extends Object> words) {
        Object randomWord = words.get(new Random().nextInt(words.size()));
        if (randomWord instanceof Weekly) {
            return ((Weekly) randomWord).getWordEntity();
        } else if (randomWord instanceof Learning) {
            return ((Learning) randomWord).getWordEntity();
        }
        throw new IllegalStateException("Unexpected word type");
    }
    private Word getRandomWordExcept(List<? extends Object> words, Word exceptWord) {
        Word randomWord;
        do {
            randomWord = getRandomWord(words);
        } while (randomWord.getWordId().equals(exceptWord.getWordId()));
        return randomWord;
    }


    // 퀴즈 결과 저장
    @Transactional
    public void saveQuizResult(String uid, String wqId, String userAnswer) {
        logger.info("퀴즈 결과 저장 시작. 사용자: {}, 퀴즈: {}, 답변: {}", uid, wqId, userAnswer);

        User user = userRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + uid));
        Wqinfo wqinfo = wqinfoRepository.findById(wqId)
                .orElseThrow(() -> new RuntimeException("문제를 찾을 수 없습니다: " + wqId));

        Wqresult result = new Wqresult();
        result.setUser(user);
        result.setWqInfo(wqinfo);
        result.setUWqA(userAnswer);
        result.setWqCheck(wqinfo.getWqAnswer().equalsIgnoreCase(userAnswer));
        result.setTime(new java.sql.Timestamp(System.currentTimeMillis()));

        Wqresult savedResult = wqresultRepository.save(result);
        logger.info("Wqresult 저장 완료. ID: {}", savedResult.getWqResultId());

        if (!result.getWqCheck()) {
            Wqwrong wrong = new Wqwrong();
            wrong.setUser(user);
            wrong.setWqInfo(wqinfo);
            wrong.setWord(wqinfo.getWord());
            Wqwrong savedWrong = wqwrongRepository.save(wrong);
            logger.info("Wqwrong 저장 완료. ID: {}", savedWrong.getWqwId());
        }

        logger.info("퀴즈 결과 저장 완료. 사용자: {}, 퀴즈: {}, 정답 여부: {}", uid, wqId, result.getWqCheck());
    }

    // 퀴즈 결과 반환
    public OneQuizCorrectDto getQuizAnswer(String uid) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + uid));

        Wqresult latestResult = wqresultRepository.findTopByUserOrderByTimeDesc(user)
                .orElseThrow(() -> new RuntimeException("최근 퀴즈 결과를 찾을 수 없습니다."));

        Wqinfo wqinfo = latestResult.getWqInfo();
        Word word = wqinfo.getWord();

        OneQuizCorrectDto correctdto = new OneQuizCorrectDto();
        correctdto.setWqId(wqinfo.getWqId());
        correctdto.setWordId(word.getWordId());
        correctdto.setWord(word.getWord());
        correctdto.setWordInfo(word.getWordInfo());
        correctdto.setCorrectAnswer(wqinfo.getWqAnswer());
        correctdto.setUserAnswer(latestResult.getUWqA());
        correctdto.setIsCorrect(latestResult.getWqCheck());

        return correctdto;
    }
}
