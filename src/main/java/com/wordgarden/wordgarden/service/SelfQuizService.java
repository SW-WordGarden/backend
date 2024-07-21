package com.wordgarden.wordgarden.service;

import com.wordgarden.wordgarden.dto.SqDTO;
import com.wordgarden.wordgarden.dto.QuestionAnswerDTO;
import com.wordgarden.wordgarden.entity.Sq;
import com.wordgarden.wordgarden.entity.Sqinfo;
import com.wordgarden.wordgarden.entity.User;
import com.wordgarden.wordgarden.repository.SqRepository;
import com.wordgarden.wordgarden.repository.SqinfoRepository;
import com.wordgarden.wordgarden.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SelfQuizService {

    @Autowired
    private SqinfoRepository sqinfoRepository;

    @Autowired
    private SqRepository sqRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public List<Long> createCustomQuiz(SqDTO sqDTO) {
        User user = userRepository.findById(sqDTO.getUid())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Long> createdSqIds = new ArrayList<>();

        for (int i = 0; i < sqDTO.getQuestionsAndAnswers().size(); i++) {
            QuestionAnswerDTO qaDTO = sqDTO.getQuestionsAndAnswers().get(i);

            Sqinfo sqinfo = new Sqinfo();
            sqinfo.setUser(user);
            sqinfo.setSqTitle(sqDTO.getQuizTitle());
            Sqinfo savedSqinfo = sqinfoRepository.save(sqinfo);

            Sq sq = new Sq();
            sq.setSqinfo(savedSqinfo);
            sq.setSqQuestion(qaDTO.getQuestion());
            sq.setSqAnswer(qaDTO.getAnswer());
            sq.setSqQnum(i + 1);
            sqRepository.save(sq);

            createdSqIds.add(savedSqinfo.getSqId());
        }

        return createdSqIds;
    }
}
