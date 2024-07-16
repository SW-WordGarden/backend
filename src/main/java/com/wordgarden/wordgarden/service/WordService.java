package com.wordgarden.wordgarden.service;


import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.wordgarden.wordgarden.dto.LearningDTO;
import com.wordgarden.wordgarden.dto.WordDTO;
import com.wordgarden.wordgarden.entity.Learning;
import com.wordgarden.wordgarden.entity.Weekly;
import com.wordgarden.wordgarden.entity.Word;
import com.wordgarden.wordgarden.repository.LearningRepository;
import com.wordgarden.wordgarden.repository.WeeklyRepository;
import com.wordgarden.wordgarden.repository.WordRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WordService {

    @Autowired
    private WordRepository wordRepository;
    @Autowired
    private LearningRepository learningRepository;
    @Autowired
    private WeeklyRepository weeklyRepository;

    @Value("${csv.file.path}")
    private String csvFilePath;

    @PostConstruct
    public void init(){
        loadWordsFromCSV();
    }

    // csv파일에서 단어 로드 후 데이터베이스 저장
    public void loadWordsFromCSV() {
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            List<String[]> records = reader.readAll();
            for (String[] record : records) {
                Word word = new Word();
                word.setWordId(record[0]);
                word.setWord(record[1]);
                word.setCategory(record[2]);
                word.setWordInfo(record[3]);
                wordRepository.save(word);
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    // DTO저장
    public List<WordDTO> getWordsByCategory(String category) {
        return wordRepository.findByCategory(category).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public List<LearningDTO> getLearningWords() {
        List<Learning> learningList = learningRepository.findAll();
        return convertToLearningDTOList(learningList);
    }
    public List<LearningDTO> getLearningWordsByCategory(String category) {
        return learningRepository.findByWordEntityCategory(category).stream()
                .map(this::convertToLearningDTO)
                .collect(Collectors.toList());
    }



    @Transactional
    public void updateLearningWords() {
        // 기존 학습 단어를 Weekly로 이동
        List<Learning> learningWords = learningRepository.findAll();
        for (Learning learning : learningWords) {
            Weekly weekly = new Weekly();
            weekly.setWord(learning.getWord());
            weekly.setWordEntity(learning.getWordEntity());
            weeklyRepository.save(weekly);
            learningRepository.delete(learning);
        }

        // 각 카테고리별로 단어를 10개씩 선택하여 Learning으로 저장
        List<String> categories = wordRepository.findDistinctCategories();
        for (String category : categories) {
            List<Word> words = wordRepository.findTop10ByCategoryOrderByWordId(category);
            for (Word word : words) {
                Learning learning = new Learning();
                learning.setWord(word.getWord());
                learning.setWordEntity(word);
                learningRepository.save(learning);
            }
        }
    }

    @Transactional
    public void cleanUpWeeklyWords() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        weeklyRepository.deleteByCreatedAtBefore(oneWeekAgo);
    }


    // DTO변환 메서드
    private WordDTO convertToDTO(Word word) {
        WordDTO dto = new WordDTO();
        dto.setWordId(word.getWordId());
        dto.setWord(word.getWord());
        dto.setCategory(word.getCategory());
        dto.setWordInfo(word.getWordInfo());
        return dto;
    }
    private LearningDTO convertToLearningDTO(Learning learning) {
        LearningDTO dto = new LearningDTO();
        dto.setId(learning.getId());
        dto.setWordId(learning.getWordEntity().getWordId());
        dto.setWord(learning.getWord());
        dto.setCategory(learning.getWordEntity().getCategory());
        dto.setWordInfo(learning.getWordEntity().getWordInfo());
        return dto;
    }
    private List<LearningDTO> convertToLearningDTOList(List<Learning> learningList) {
        return learningList.stream()
                .map(this::convertToLearningDTO)
                .collect(Collectors.toList());
    }
}
