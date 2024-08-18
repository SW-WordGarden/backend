package com.wordgarden.wordgarden.service;

import com.wordgarden.wordgarden.entity.Alarm;
import com.wordgarden.wordgarden.entity.Sqinfo;
import com.wordgarden.wordgarden.entity.User;
import com.wordgarden.wordgarden.entity.Wqinfo;
import com.wordgarden.wordgarden.repository.AlarmRepository;
import com.wordgarden.wordgarden.repository.SqinfoRepository;
import com.wordgarden.wordgarden.repository.UserRepository;
import com.wordgarden.wordgarden.repository.WqinfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SharingService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WqinfoRepository wqinfoRepository;
    @Autowired
    private SqinfoRepository sqinfoRepository;
    @Autowired
    private FCMNotificationService fcmNotificationService;
    @Autowired
    private AlarmRepository alarmRepository;

    @Transactional
    public void shareQuiz(String fromUserId, String toUserId, String quizId) {
        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new RuntimeException("From user not found"));
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new RuntimeException("To user not found"));

        String quizContent;
        String quizType;

        if (quizId.startsWith("앱 퀴즈_")) {
            quizContent = quizId; // WQ의 경우 quizId가 곧 wq_title
            quizType = "WQ";
        } else {
            // SQ의 경우 quizId를 그대로 사용
            quizContent = quizId;
            quizType = "SQ";
        }

        // FCM 알림 전송
        fcmNotificationService.sendQuizShareNotification(fromUser, toUser, quizContent, quizType);

        // 알람 저장
        Alarm alarm = new Alarm();
        alarm.setFromUser(fromUser);
        alarm.setToUser(toUser);
        alarm.setContent(quizContent);
        alarm.setIsRead(false);
        alarm.setCreateTime(LocalDateTime.now());
        alarmRepository.save(alarm);
    }
}
