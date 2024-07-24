package com.wordgarden.wordgarden.service;

import com.wordgarden.wordgarden.dto.SqDTO;
import com.wordgarden.wordgarden.dto.QuestionAnswerDTO;
import com.wordgarden.wordgarden.dto.SqresultDTO;
import com.wordgarden.wordgarden.entity.Sq;
import com.wordgarden.wordgarden.entity.Sqinfo;
import com.wordgarden.wordgarden.entity.Sqresult;
import com.wordgarden.wordgarden.entity.User;
import com.wordgarden.wordgarden.repository.SqRepository;
import com.wordgarden.wordgarden.repository.SqinfoRepository;
import com.wordgarden.wordgarden.repository.SqresultRepository;
import com.wordgarden.wordgarden.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SelfQuizService {

    @Autowired
    private SqinfoRepository sqinfoRepository;

    @Autowired
    private SqRepository sqRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SqresultRepository sqresultRepository;

    @Transactional
    public List<Long> createCustomQuiz(SqDTO sqDTO) {
        User user = userRepository.findById(sqDTO.getUid())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (sqinfoRepository.existsByUserAndSqTitle(user, sqDTO.getQuizTitle())) {
            throw new RuntimeException("A quiz with this title already exists for this user");
        }

        Sqinfo sqinfo = new Sqinfo();
        sqinfo.setUser(user);
        sqinfo.setSqTitle(sqDTO.getQuizTitle());
        Sqinfo savedSqinfo = sqinfoRepository.save(sqinfo);

        List<Long> createdSqIds = new ArrayList<>();

        for (int i = 0; i < sqDTO.getQuestionsAndAnswers().size(); i++) {
            QuestionAnswerDTO qaDTO = sqDTO.getQuestionsAndAnswers().get(i);

            Sq sq = new Sq();
            sq.setSqinfo(savedSqinfo);
            sq.setSqQuestion(qaDTO.getQuestion());
            sq.setSqAnswer(qaDTO.getAnswer());
            sq.setSqQnum(i + 1);
            sq.setSqTitle(sqDTO.getQuizTitle());  // 여기에 타이틀 설정 추가
            Sq savedSq = sqRepository.save(sq);

            createdSqIds.add(savedSq.getId());
        }

        return createdSqIds;
    }

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

}
