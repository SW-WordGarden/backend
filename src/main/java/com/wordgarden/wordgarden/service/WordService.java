package com.wordgarden.wordgarden.service;

import com.wordgarden.wordgarden.entity.Like;
import com.wordgarden.wordgarden.entity.Word;
import com.wordgarden.wordgarden.repository.LikeRepository;
import com.wordgarden.wordgarden.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class WordService {

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private LikeRepository likeRepository;

    @value("${csv.file.path}")
    private String csvFilePath;

    @Value("${naver.api.url}")
    private String naverApiUrl;

    @Value("${naver.api.clientId}")
    private String clientId;

    @Value("${naver.api.clientSecret}")
    private String clientSecret;

    // csv 저장
    public void loadWordsFromCSV() {
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            List<String[]> records = reader.readAll();
            for (String[] record : records) {
                Word word = new Word();
                word.setWordId(record[0]);
                word.setWord(record[1]);
                word.setCategory(record[2]);
                wordRepository.save(word);
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    // 주어진 카테고리에서 10개의 무작위 단어 반환
    public List<Word> getWeeklyWordsByCategory(String category) {
        List<Word> words = wordRepository.findByCategory(category);
        Random random = new Random();
        return random.ints(0, words.size())
                .distinct()
                .limit(10)
                .mapToObj(words::get)
                .collect(Collectors.toList());
    }

    // 네이버 API를 사용하여 단어 정보를 가져옴
    public Word getWordInfo(String word) {
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("%s?query=%s", naverApiUrl, word);
        Word response = restTemplate.getForObject(url, Word.class, clientId, clientSecret);
        return response;
    }

    //좋아요한 단어처리
    public void likeWord(String wordId, String uid) {
        Like like = new Like();
        like.setWordId(wordId);
        like.setUid(uid);
        likeRepository.save(like);
    }
}
}
