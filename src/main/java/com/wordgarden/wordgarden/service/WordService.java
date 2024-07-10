package com.wordgarden.wordgarden.service;


import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.wordgarden.wordgarden.entity.Word;
import com.wordgarden.wordgarden.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Service
public class WordService {

    // csv파일에서 단어 로드 후 데이터베이스 저장
    // 미리 해둘 것이기 때문에 호출하시면 안되욤!!!!!
    @Autowired
    private WordRepository wordRepository;
    @Value("${csv.file.path}")
    private String csvFilePath;
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
}
