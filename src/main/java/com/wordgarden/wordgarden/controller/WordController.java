package com.wordgarden.wordgarden.controller;

import com.wordgarden.wordgarden.Service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/word")
public class WordController {

    @Autowired
    private WordService wordService;

    @PostMapping("/load")
    public void loadWordsFromCSV(){
        wordService.loadWordsFromCSV();
    }
}
