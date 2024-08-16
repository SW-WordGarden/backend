package com.wordgarden.wordgarden.service;

import com.wordgarden.wordgarden.entity.Wqinfo;
import com.wordgarden.wordgarden.entity.Word;
import com.wordgarden.wordgarden.repository.WqinfoRepository;
import com.wordgarden.wordgarden.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class InitializationService {

    private final WqinfoRepository wqinfoRepository;
    private final WordRepository wordRepository;

    @Autowired
    public InitializationService(WqinfoRepository wqinfoRepository, WordRepository wordRepository) {
        this.wqinfoRepository = wqinfoRepository;
        this.wordRepository = wordRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeQuestions() {
        if (wqinfoRepository.count() > 0) {
            return; // 이미 문제가 있다면 초기화하지 않습니다.
        }

        List<Word> allWords = wordRepository.findAll();
        Collections.shuffle(allWords);

        List<Wqinfo> questions = new ArrayList<>();
        questions.addAll(generateQuestionsOfType("write", 500, allWords));
        questions.addAll(generateQuestionsOfType("four", 1000, allWords));
        questions.addAll(generateQuestionsOfType("ox", 1000, allWords));

        wqinfoRepository.saveAll(questions);
    }

    private List<Wqinfo> generateQuestionsOfType(String type, int count, List<Word> words) {
        List<Wqinfo> questions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Wqinfo question = new Wqinfo();
            question.setWqId(type + i);
            question.setWord(words.get(i % words.size()));
            // 여기에 문제 생성 로직 추가 (type에 따라 다르게)
            questions.add(question);
        }
        return questions;
    }
}