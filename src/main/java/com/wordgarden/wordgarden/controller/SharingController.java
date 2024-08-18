package com.wordgarden.wordgarden.controller;

import com.wordgarden.wordgarden.dto.ShareRequestDto;
import com.wordgarden.wordgarden.entity.Sqinfo;
import com.wordgarden.wordgarden.entity.User;
import com.wordgarden.wordgarden.entity.Wqinfo;
import com.wordgarden.wordgarden.repository.SqinfoRepository;
import com.wordgarden.wordgarden.repository.UserRepository;
import com.wordgarden.wordgarden.repository.WqinfoRepository;
import com.wordgarden.wordgarden.service.FCMNotificationService;
import com.wordgarden.wordgarden.service.SharingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/share")
public class SharingController {

    private static final Logger logger = LoggerFactory.getLogger(SharingController.class);

    @Autowired
    private SharingService sharingService;

    @PostMapping("/quiz")
    public ResponseEntity<?> shareQuiz(@RequestBody ShareRequestDto request) {
        try {
            sharingService.shareQuiz(request.getFromUserId(), request.getToUserId(), request.getQuizId());
            return ResponseEntity.ok().body("Quiz shared successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to share quiz: " + e.getMessage());
        }
    }
}