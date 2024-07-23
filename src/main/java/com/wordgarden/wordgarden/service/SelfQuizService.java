package com.wordgarden.wordgarden.service;

import com.wordgarden.wordgarden.dto.SqDTO;
import com.wordgarden.wordgarden.dto.QuestionAnswerDTO;
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
import java.util.List;
import java.util.stream.Collectors;

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

    public List<SqDTO> getCustomQuizzesByUser(String uid) {
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Sqinfo> sqinfos = sqinfoRepository.findByUser(user);
        List<SqDTO> quizzes = new ArrayList<>();

        for (Sqinfo sqinfo : sqinfos) {
            SqDTO sqDTO = new SqDTO();
            sqDTO.setQuizTitle(sqinfo.getSqTitle());
            sqDTO.setUid(uid);

            List<QuestionAnswerDTO> questionsAndAnswers = new ArrayList<>();
            List<Sq> sqs = sqRepository.findBySqinfoOrderBySqQnum(sqinfo);

            for (Sq sq : sqs) {
                QuestionAnswerDTO qaDTO = new QuestionAnswerDTO();
                qaDTO.setQuestion(sq.getSqQuestion());
                qaDTO.setAnswer(sq.getSqAnswer());
                questionsAndAnswers.add(qaDTO);
            }

            sqDTO.setQuestionsAndAnswers(questionsAndAnswers);
            quizzes.add(sqDTO);
        }

        return quizzes;
    }

    public List<SqDTO> getCreatedQuizzesByUser(String uid) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Sqinfo> sqinfos = sqinfoRepository.findByUser(user);
        return convertSqinfosToSqDTOs(sqinfos);
    }

    public List<SqDTO> getSolvedQuizzesByUser(String uid) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Sqresult> sqresults = sqresultRepository.findByUser(user);
        List<Sqinfo> sqinfos = sqresults.stream()
                .map(Sqresult::getSqinfo)
                .distinct()
                .collect(Collectors.toList());
        return convertSqinfosToSqDTOs(sqinfos);
    }

    private List<SqDTO> convertSqinfosToSqDTOs(List<Sqinfo> sqinfos) {
        List<SqDTO> quizzes = new ArrayList<>();

        for (Sqinfo sqinfo : sqinfos) {
            SqDTO sqDTO = new SqDTO();
            sqDTO.setQuizTitle(sqinfo.getSqTitle());
            sqDTO.setUid(sqinfo.getUser().getUid());

            List<QuestionAnswerDTO> questionsAndAnswers = new ArrayList<>();
            List<Sq> sqs = sqRepository.findBySqinfoOrderBySqQnum(sqinfo);

            for (Sq sq : sqs) {
                QuestionAnswerDTO qaDTO = new QuestionAnswerDTO();
                qaDTO.setQuestion(sq.getSqQuestion());
                qaDTO.setAnswer(sq.getSqAnswer());
                questionsAndAnswers.add(qaDTO);
            }

            sqDTO.setQuestionsAndAnswers(questionsAndAnswers);
            quizzes.add(sqDTO);
        }

        return quizzes;
    }
}
