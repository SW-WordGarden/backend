package com.wordgarden.wordgarden.controller;

import com.wordgarden.wordgarden.dto.AlarmDTO;
import com.wordgarden.wordgarden.dto.ShareRequestDto;
import com.wordgarden.wordgarden.entity.Sqinfo;
import com.wordgarden.wordgarden.entity.User;
import com.wordgarden.wordgarden.entity.Wqinfo;
import com.wordgarden.wordgarden.exception.UserNotFoundException;
import com.wordgarden.wordgarden.repository.SqinfoRepository;
import com.wordgarden.wordgarden.repository.UserRepository;
import com.wordgarden.wordgarden.repository.WqinfoRepository;
import com.wordgarden.wordgarden.service.FCMNotificationService;
import com.wordgarden.wordgarden.service.SharingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/share")
public class SharingController {

    private static final Logger log = LoggerFactory.getLogger(SharingController.class);

    @Autowired
    private SharingService sharingService;

    @PostMapping("/quiz")
    public ResponseEntity<?> shareQuiz(@RequestBody @Valid ShareRequestDto request) {
        try {
            String result = sharingService.shareQuiz(request.getFromUserId(), request.getToUserId(), request.getQuizId());
            return ResponseEntity.ok().body(result);
        } catch (UserNotFoundException e) {
            log.error("User not found while sharing quiz", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to share quiz", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to share quiz: " + e.getMessage());
        }
    }

    @GetMapping("/alarms/{userId}")
    public ResponseEntity<?> getAlarmList(@PathVariable String userId) {
        try {
            List<AlarmDTO> alarms = sharingService.getAlarmList(userId);
            return ResponseEntity.ok(alarms);
        } catch (UserNotFoundException e) {
            log.error("User not found while fetching alarm list", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to fetch alarm list", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch alarm list: " + e.getMessage());
        }
    }
}