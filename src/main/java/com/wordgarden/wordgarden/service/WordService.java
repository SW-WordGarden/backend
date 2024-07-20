package com.wordgarden.wordgarden.service;


import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.wordgarden.wordgarden.dto.LearningDTO;
import com.wordgarden.wordgarden.dto.WordDTO;
import com.wordgarden.wordgarden.entity.*;
import com.wordgarden.wordgarden.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WordService {

    @Autowired
    private WordRepository wordRepository;
    @Autowired
    private LearningRepository learningRepository;
    @Autowired
    private WeeklyRepository weeklyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LikeRepository likeRepository;

//    @Value("${csv.file.path}")
    @Value("C:\\python\\test_word.csv")
    private String csvFilePath;

    @PostConstruct
    public void init(){
        loadWordsFromCSV();
    }

    // csv파일에서 단어 로드 후 데이터베이스 저장
    public void loadWordsFromCSV() {
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath, StandardCharsets.UTF_8))) {
            List<String[]> records = reader.readAll();
            for (String[] record : records) {
                Word word = new Word();
                word.setCategory(record[0]);
                word.setWord(record[1]);
                word.setWordInfo(record[2]);
                word.setThumbnail(record[3]);
                word.setWordId(record[4]);
                wordRepository.save(word);
            }
            // 단어 저장 후 Learning 생성
            generateInitialLearning();
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    // Learning 초기 생성
    @Transactional
    public void generateInitialLearning() {
        // 기존 Learning 데이터 모두 삭제
        learningRepository.deleteAll();

        // 모든 카테고리에 대해 최근 10개의 단어를 아이디 순서로 Learning으로 만듦
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

    // 매주 7일마다 Learning 업데이트
    @Transactional
    @Scheduled(cron = "0 0 0 */7 * *") // 매주 7일마다 실행
    public void updateLearningWords() {
        // 기존 Learning 데이터 모두 삭제
        learningRepository.deleteAll();

        // 모든 카테고리에 대해 최근 10개의 단어를 아이디 순서로 Learning으로 만듦
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

    // Learning 테이블의 모든 단어를 조회하는 메서드
    public List<WordDTO> getAllWords() {
        List<Learning> learningList = learningRepository.findAll();
        return convertToWordDTOList(learningList);
    }

    // Learning을 조회하여 DTO 리스트로 변환하여 반환
    public List<LearningDTO> getLearningWords() {
        List<Learning> learningList = learningRepository.findAll();
        return convertToLearningDTOList(learningList);
    }

    // 카테고리별 Learning을 조회하여 DTO 리스트로 변환하여 반환
    public List<LearningDTO> getLearningWordsByCategory(String category) {
        List<Learning> learningList = learningRepository.findByWordEntityCategory(category);
        return convertToLearningDTOList(learningList);
    }

    // Weekly 테이블을 초기화하고 지난주 Learning을 기반으로 Weekly 데이터 생성
    @Transactional
    @Scheduled(cron = "0 0 0 */7 * *") // 매주 7일마다 실행
    public void cleanUpWeeklyWords() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        List<Learning> learningList = learningRepository.findAll();
        for (Learning learning : learningList) {
            Weekly weekly = new Weekly();
            weekly.setWord(learning.getWord());
            weekly.setWordEntity(learning.getWordEntity());
            weekly.setCreatedAt(LocalDateTime.now());
            weeklyRepository.save(weekly);
        }
        learningRepository.deleteAll(); // Learning 데이터 삭제
    }

    // DTO 변환 메서드들
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

    private WordDTO convertToLearningWordDTO(Learning learning) {
        WordDTO dto = new WordDTO();
        dto.setWordId(learning.getWordEntity().getWordId());
        dto.setWord(learning.getWordEntity().getWord());
        dto.setCategory(learning.getWordEntity().getCategory());
        dto.setWordInfo(learning.getWordEntity().getWordInfo());
        return dto;
    }

    private List<WordDTO> convertToWordDTOList(List<Learning> learningList) {
        return learningList.stream()
                .map(this::convertToLearningWordDTO)
                .collect(Collectors.toList());
    }

    // 좋아요 토글
    @Transactional
    public boolean toggleLike(String uid, String wordId) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new RuntimeException("Word not found"));

        Like like = likeRepository.findByUserAndWord(user, word);
        if (like == null) {
            // 좋아요가 없으면 추가
            like = new Like();
            like.setUser(user);
            like.setWord(word);
            likeRepository.save(like);
            return true;
        } else {
            // 좋아요가 있으면 제거
            likeRepository.delete(like);
            return false;
        }
    }

    // 사용자의 좋아요 리스트 조회
    public List<WordDTO> getLikedWords(String uid) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Like> likes = likeRepository.findByUser(user);
        return likes.stream()
                .map(like -> convertWordToDTO(like.getWord()))
                .collect(Collectors.toList());
    }

    // 특정 단어의 좋아요 상태 확인
    public boolean checkLikeStatus(String uid, String wordId) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new RuntimeException("Word not found"));
        return likeRepository.findByUserAndWord(user, word) != null;
    }

    private WordDTO convertWordToDTO(Word word) {
        WordDTO dto = new WordDTO();
        dto.setWordId(word.getWordId());
        dto.setWord(word.getWord());
        dto.setCategory(word.getCategory());
        dto.setWordInfo(word.getWordInfo());
        // 필요한 다른 필드들도 설정
        return dto;
    }
}
