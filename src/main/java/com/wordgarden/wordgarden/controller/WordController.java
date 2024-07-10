package com.wordgarden.wordgarden.controller;

import com.wordgarden.wordgarden.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/word")
public class WordController {

    @Autowired
    private WordService wordService;

    // csv파일에서 단어 로드 후 데이터베이스 저장
    // 미리 해둘 것이기 때문에 호출하시면 안되욤!!!!!
    @PostMapping("/load")
    public void loadWordsFromCSV(){
        wordService.loadWordsFromCSV();
    }

}
