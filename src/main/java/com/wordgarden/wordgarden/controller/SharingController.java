package com.wordgarden.wordgarden.controller;

import com.wordgarden.wordgarden.dto.AlarmDTO;
import com.wordgarden.wordgarden.dto.AlarmDetailDTO;
import com.wordgarden.wordgarden.dto.ShareRequestDto;
import com.wordgarden.wordgarden.exception.UnauthorizedException;
import com.wordgarden.wordgarden.exception.UserNotFoundException;
import com.wordgarden.wordgarden.exception.AlarmNotFoundException;
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
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

    // 알람 상세
    @GetMapping("/alarmdetail/{alarmId}")
    public ResponseEntity<?> getAlarmById(@PathVariable String alarmId) {
        try {
            AlarmDetailDTO alarmDetail = sharingService.getAlarmById(alarmId);
            return ResponseEntity.ok(alarmDetail);
        } catch (AlarmNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch alarm details: " + e.getMessage());
        }
    }

    // 알람 삭제
    @DeleteMapping("/alarmdelete/{alarmId}")
    public ResponseEntity<?> deleteAlarm(@PathVariable String alarmId, @RequestParam String userId) {
        try {
            sharingService.deleteAlarm(alarmId, userId);
            return ResponseEntity.ok("Alarm deleted successfully");
        } catch (AlarmNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete alarm: " + e.getMessage());
        }
    }
}