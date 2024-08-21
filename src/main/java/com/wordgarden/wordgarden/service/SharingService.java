package com.wordgarden.wordgarden.service;

import com.wordgarden.wordgarden.dto.AlarmDTO;
import com.wordgarden.wordgarden.entity.Alarm;
import com.wordgarden.wordgarden.entity.Sqinfo;
import com.wordgarden.wordgarden.entity.User;
import com.wordgarden.wordgarden.entity.Wqinfo;
import com.wordgarden.wordgarden.exception.UserNotFoundException;
import com.wordgarden.wordgarden.repository.AlarmRepository;
import com.wordgarden.wordgarden.repository.SqinfoRepository;
import com.wordgarden.wordgarden.repository.UserRepository;
import com.wordgarden.wordgarden.repository.WqinfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SharingService {
    private static final Logger log = LoggerFactory.getLogger(SharingService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AlarmRepository alarmRepository;
    @Autowired
    private FCMNotificationService fcmNotificationService;

    @Transactional
    public String shareQuiz(String fromUserId, String toUserId, String quizId) {
        log.info("Sharing quiz: {} from user: {} to user: {}", quizId, fromUserId, toUserId);

        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new UserNotFoundException("From user not found: " + fromUserId));
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new UserNotFoundException("To user not found: " + toUserId));

        String quizType = quizId.startsWith("앱 퀴즈_") ? "WQ" : "SQ";

        // Save alarm to database
        Alarm alarm = new Alarm();
        alarm.setFromUser(fromUser);
        alarm.setToUser(toUser);
        alarm.setContent(quizId);
        alarm.setIsRead(false);
        alarm.setCreateTime(LocalDateTime.now());
        alarmRepository.save(alarm);
        log.info("Alarm saved to database: {}", alarm.getAlarmId());

        // Send FCM notification
        String fcmResult = fcmNotificationService.sendQuizShareNotification(fromUser, toUser, quizId, quizType);
        log.info("FCM notification result: {}", fcmResult);

        return fcmResult;
    }

    public List<AlarmDTO> getAlarmList(String userId) {
        log.info("Fetching alarm list for user: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        List<Alarm> alarms = alarmRepository.findByToUserOrderByCreateTimeDesc(user);
        return alarms.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private AlarmDTO convertToDTO(Alarm alarm) {
        return new AlarmDTO(
                alarm.getAlarmId(),
                alarm.getContent(),
                alarm.getIsRead(),
                alarm.getCreateTime(),
                alarm.getFromUser().getUName(),
                alarm.getToUser().getUName()
        );
    }
}