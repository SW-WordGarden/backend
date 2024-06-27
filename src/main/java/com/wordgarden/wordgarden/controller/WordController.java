package com.wordgarden.wordgarden.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/*
단어 관련 컨트롤러

 */
@
@Controller
@RequestMapping("/word")
public class WordController {
    //  주간 단어 리스트
    @GetMapping("/weeklylist")

}
