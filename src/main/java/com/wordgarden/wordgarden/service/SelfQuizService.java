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

    public List<SqDTO> getCreatedQuizzesByUser(String uid) {
        List<Sqinfo> sqinfos = sqinfoRepository.findByUserUidOrderBySqTitleAsc(uid);
        return convertSqinfosToSqDTOs(sqinfos);
    }

    private List<SqDTO> convertSqinfosToSqDTOs(List<Sqinfo> sqinfos) {
        List<SqDTO> result = new ArrayList<>();

        for (Sqinfo sqinfo : sqinfos) {
            SqDTO sqDTO = new SqDTO();
            sqDTO.setUid(sqinfo.getUser().getUid());
            sqDTO.setQuizTitle(sqinfo.getSqTitle());
            sqDTO.setQuestionsAndAnswers(new ArrayList<>());

            List<Sq> sqs = sqRepository.findBySqinfoOrderBySqQnumAsc(sqinfo);
            for (Sq sq : sqs) {
                QuestionAnswerDTO qaDTO = new QuestionAnswerDTO();
                qaDTO.setQuestion(sq.getSqQuestion());
                qaDTO.setAnswer(sq.getSqAnswer());
                qaDTO.setSqQnum(sq.getSqQnum());
                sqDTO.getQuestionsAndAnswers().add(qaDTO);
            }

            result.add(sqDTO);
        }

        return result;
    }

    public List<SqDTO> getSolvedQuizzesByUser(String uid) {
        List<Sqresult> sqresults = sqresultRepository.findSolvedQuizzesByUser(uid);
        return convertSqresultsToSqDTOs(sqresults);
    }

    private List<SqDTO> convertSqresultsToSqDTOs(List<Sqresult> sqresults) {
        Map<String, SqDTO> quizMap = new LinkedHashMap<>();

        for (Sqresult sqresult : sqresults) {
            Sq sq = sqresult.getSqinfo().getSq();
            SqDTO sqDTO = quizMap.computeIfAbsent(sq.getSqTitle(), k -> {
                SqDTO newDto = new SqDTO();
                newDto.setUid(sqresult.getUser().getUid());
                newDto.setQuizTitle(sq.getSqTitle());
                newDto.setQuestionsAndAnswers(new ArrayList<>());
                newDto.setSqresults(new ArrayList<>()); // 새로운 리스트 초기화
                return newDto;
            });

            SqresultDTO sqresultDTO = new SqresultDTO();
            sqresultDTO.setQuestion(sq.getSqQuestion());
            sqresultDTO.setAnswer(sq.getSqAnswer());
            sqresultDTO.setSqQnum(sq.getSqQnum());
            sqresultDTO.setUserAnswer(sqresult.getUSqA());
            sqresultDTO.setCorrect(sqresult.getSqCheck());
            sqDTO.getSqresults().add(sqresultDTO); // SqDTO에 SqresultDTO 추가
        }

        return new ArrayList<>(quizMap.values());
    }
}
