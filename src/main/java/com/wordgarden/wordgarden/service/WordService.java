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
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Value("${csv.file.path}")
    private String csvFilePath;

    // 초기화
    @PostConstruct
    public void init() {
        try {
            loadWordsFromCSV();
            generateInitialLearning();
        } catch (IOException | CsvException e) {
            logger.error("Failed to initialize WordService", e);
        }
    }

     // CSV 파일에서 단어를 로드하여 Word 테이블에 저장합니다.
    @Transactional
    public void loadWordsFromCSV() throws IOException, CsvException {
        logger.info("Starting to load words from CSV file: {}", csvFilePath);

        // ClassPathResource를 사용하여 리소스 폴더에서 파일을 로드
        try (InputStream inputStream = new ClassPathResource(csvFilePath).getInputStream();
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             CSVReader csvReader = new CSVReader(reader)) {

            List<String[]> records = csvReader.readAll();
            logger.info("Read {} records from CSV file", records.size());
            int savedCount = 0;

            // 헤더를 제외하고 1부터 시작
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
        } catch (FileNotFoundException e) {
            logger.error("CSV file not found: {}", csvFilePath, e);
            throw e;
        } catch (IOException e) {
            logger.error("Error reading CSV file: {}", csvFilePath, e);
            throw e;
        }
    }

    // 초기 Learning 생성
    @Transactional
    public void generateInitialLearning() {
        logger.info("Generating initial learning words");
        learningRepository.deleteAll();
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
        logger.info("Initial learning words generated");
    }

    // 7일 마다 learning 단어 업데이트
    @Transactional
    @Scheduled(cron = "0 0 0 */7 * *") // 7일마다 실행
    public void updateLearningWords() {
        logger.info("Updating learning words");
        // 현재 Learning 단어를 Weekly로 이동
        List<Learning> currentLearning = learningRepository.findAll();
        for (Learning learning : currentLearning) {
            Weekly weekly = new Weekly();
            weekly.setWord(learning.getWord());
            weekly.setWordEntity(learning.getWordEntity());
            weekly.setCreatedAt(LocalDateTime.now());
            weeklyRepository.save(weekly);
        }

        // 현재 Learning 단어 삭제
        learningRepository.deleteAll();

        // 새로운 Learning 단어 생성
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
        logger.info("Learning words updated");
    }

    // Learning 테이블 조회
    public List<LearningDTO> getLearningWords() {
        List<Learning> learningWords = learningRepository.findAll();
        if (learningWords.isEmpty()) {
            logger.info("No learning words found, generating random words");
            return getRandomLearningWords();
        }
        return learningWords.stream()
                .map(this::convertToLearningDTO)
                .collect(Collectors.toList());
    }

    // 카테고리별 10개의 데이터 선택
    private List<LearningDTO> getRandomLearningWords() {
        List<String> categories = wordRepository.findDistinctCategories();
        List<LearningDTO> randomWords = new ArrayList<>();
        for (String category : categories) {
            List<Word> words = wordRepository.findRandomWordsByCategory(category, 10);
            randomWords.addAll(words.stream()
                    .map(this::convertToLearningDTO)
                    .collect(Collectors.toList()));
        }
        return randomWords;
    }

    // 카테고리 별 단어 조회
    public List<LearningDTO> getLearningWordsByCategory(String category) {
        List<Word> words = wordRepository.findByCategory(category);
        return words.stream()
                .map(this::convertToLearningDTO)
                .collect(Collectors.toList());
    }

    // weekly 테이블 생성
    @Transactional
    @Scheduled(cron = "0 0 0 */7 * *") // 7일마다 실행
    public void cleanUpWeeklyWords() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        weeklyRepository.deleteByCreatedAtBefore(oneWeekAgo);
        // Weekly 데이터는 updateLearningWords() 메서드에서 이미 생성되므로 여기서는 생성하지 않습니다.
    }


    private LearningDTO convertToLearningDTO(Learning learning) {
        LearningDTO dto = new LearningDTO();
        dto.setId(learning.getId());
        dto.setWord(learning.getWord());
        dto.setWordId(learning.getWordEntity().getWordId());
        dto.setCategory(learning.getWordEntity().getCategory());
        dto.setWordInfo(learning.getWordEntity().getWordInfo());
        return dto;
    }


    private LearningDTO convertToLearningDTO(Word word) {
        LearningDTO dto = new LearningDTO();
        dto.setId(Long.parseLong(word.getWordId().replaceAll("\\D+", "")));
        dto.setWord(word.getWord());
        dto.setWordId(word.getWordId());
        dto.setCategory(word.getCategory());
        dto.setWordInfo(word.getWordInfo());
        return dto;
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

    // 특정 단어 조회
    @Transactional(readOnly = true)
    public WordDTO getWordById(String wordId) {
        Word word = wordRepository.findById(wordId).orElse(null);
        return word != null ? convertWordToDTO(word) : null;
    }

    // 좋아요 리스트 조회
    public List<WordDTO> getLikedWords(String uid) {
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + uid));

        List<Word> likedWords = likeRepository.findLikedWordsByUser(user);

        return likedWords.stream()
                .map(this::convertToWordDTO)
                .collect(Collectors.toList());
    }

    // 특정 단어 좋아요 상태 확인
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

    private WordDTO convertToWordDTO(Word word) {
        WordDTO dto = new WordDTO();
        dto.setWordId(word.getWordId());
        dto.setWord(word.getWord());
        dto.setCategory(word.getCategory());
        dto.setWordInfo(word.getWordInfo());
        return dto;
    }

    // Learning에서 단어 하나 추출하는 api
    @Transactional(readOnly = true)
    public WordDTO getRandomLearningWord() {
        Learning randomLearning = learningRepository.findRandomLearning();
        return randomLearning != null ? convertWordToDTO(randomLearning.getWordEntity()) : null;
    }


}