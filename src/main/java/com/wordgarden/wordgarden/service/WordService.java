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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(WordService.class);

//    @Value("${csv.file.path}")
    @Value("C:\\Users\\ENC-NP-1597\\Downloads\\complete_word.csv")
    private String csvFilePath;

    @PostConstruct
    public void init() {
        try {
            loadWordsFromCSV();
        } catch (IOException e) {
            System.err.println("Failed to load words from CSV due to IO error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize WordService due to IO error", e);
        } catch (CsvException e) {
            System.err.println("Failed to parse CSV file: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize WordService due to CSV parsing error", e);
        }
    }

    // csv파일에서 단어 로드 후 데이터베이스 저장
    @Transactional
    public void loadWordsFromCSV() throws IOException, CsvException {
        logger.info("Starting to load words from CSV file: {}", csvFilePath);
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath, StandardCharsets.UTF_8))) {
            List<String[]> records = reader.readAll();
            logger.info("Read {} records from CSV file", records.size());
            int savedCount = 0;
            // Skip the header
            for (int i = 1; i < records.size(); i++) {
                String[] record = records.get(i);
                if (record.length < 4) {
                    logger.warn("Invalid record: {}", String.join(", ", record));
                    continue;
                }
                Word word = new Word();
                word.setCategory(record[0]);
                word.setWord(record[1]);
                word.setWordInfo(record[2]);
                word.setWordId(record[3]);
                try {
                    wordRepository.save(word);
                    savedCount++;
                } catch (Exception e) {
                    logger.error("Error saving word: {}", word.getWord(), e);
                }
            }
            logger.info("Saved {} words to the database", savedCount);
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
        List<Word> words = wordRepository.findAll();
        return words.stream()
            .map(this::convertToLearningDTO)
            .collect(Collectors.toList());
    }

    // 카테고리별 Learning을 조회하여 DTO 리스트로 변환하여 반환
    public List<LearningDTO> getLearningWordsByCategory(String category) {
        List<Word> words = wordRepository.findByCategory(category);
        return words.stream()
            .map(this::convertToLearningDTO)
            .collect(Collectors.toList());
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
    private LearningDTO convertToLearningDTO(Word word) {
        LearningDTO dto = new LearningDTO();
        dto.setId(Long.parseLong(word.getWordId().replaceAll("\\D+", "")));  // Extract numeric part
        dto.setWord(word.getWord());
        dto.setWordId(word.getWordId());
        dto.setCategory(word.getCategory());
        dto.setWordInfo(word.getWordInfo());
        return dto;
    }

//    private List<LearningDTO> convertToLearningDTOList(List<Learning> learningList) {
//        return learningList.stream()
//                .map(this::convertToLearningDTO)
//                .collect(Collectors.toList());
//    }

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
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + uid));
        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new RuntimeException("단어를 찾을 수 없습니다: " + wordId));

        Like like = likeRepository.findByUserAndWord(user, word);
        if (like != null) {
            likeRepository.delete(like);
            return false;
        } else {
            Like newLike = new Like();
            newLike.setUser(user);
            newLike.setWord(word);
            likeRepository.save(newLike);
            return true;
        }
    }

    @Transactional(readOnly = true)
    public WordDTO getWordById(String wordId) {
        Word word = wordRepository.findById(wordId).orElse(null);
        return word != null ? convertWordToDTO(word) : null;
    }

    // 사용자의 좋아요 리스트 조회
    public List<WordDTO> getLikedWords(String uid) {
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + uid));

        List<Word> likedWords = likeRepository.findLikedWordsByUser(user);

        return likedWords.stream()
                .map(this::convertToWordDTO)
                .collect(Collectors.toList());
    }

    // 특정 단어의 좋아요 상태 확인
    public boolean checkLikeStatus(String uid, String wordId) {
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + uid));
        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new RuntimeException("단어를 찾을 수 없습니다: " + wordId));

        return likeRepository.findByUserAndWord(user, word) != null;
    }

    private WordDTO convertWordToDTO(Word word) {
        WordDTO dto = new WordDTO();
        dto.setWordId(word.getWordId());
        dto.setWord(word.getWord());
        dto.setCategory(word.getCategory());
        dto.setWordInfo(word.getWordInfo());
        return dto;
    }

    // 좋아요에서 사용
    private WordDTO convertToWordDTO(Word word) {
        WordDTO dto = new WordDTO();
        dto.setWordId(word.getWordId());
        dto.setWord(word.getWord());
        dto.setCategory(word.getCategory());
        dto.setWordInfo(word.getWordInfo());
        return dto;
    }
}
