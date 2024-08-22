package com.wordgarden.wordgarden.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.wordgarden.wordgarden.dto.AlarmDTO;
import com.wordgarden.wordgarden.dto.AlarmDetailDTO;
import com.wordgarden.wordgarden.entity.Alarm;
import com.wordgarden.wordgarden.entity.User;
import com.wordgarden.wordgarden.entity.Wqinfo;
import com.wordgarden.wordgarden.exception.AlarmNotFoundException;
import com.wordgarden.wordgarden.exception.UnauthorizedException;
import com.wordgarden.wordgarden.exception.UserNotFoundException;
import com.wordgarden.wordgarden.repository.AlarmRepository;
import com.wordgarden.wordgarden.repository.SqinfoRepository;
import com.wordgarden.wordgarden.repository.UserRepository;
import com.wordgarden.wordgarden.repository.WqinfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


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
    @Autowired
    private FirebaseMessaging firebaseMessaging;
    @Autowired
    private WqinfoRepository wqinfoRepository;
    @Autowired
    private SqinfoRepository sqinfoRepository;

    // 퀴즈 공유
    public String shareQuiz(String fromUserId, String toUserId, String quizId) {
        log.info("Sharing quiz: {} from user: {} to user: {}", quizId, fromUserId, toUserId);

        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new UserNotFoundException("From user not found: " + fromUserId));
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new UserNotFoundException("To user not found: " + toUserId));

        String quizType = determineQuizType(quizId);

        // 1. 알람 생성 및 DB 저장
        Alarm savedAlarm = createAndSaveAlarm(fromUser, toUser, quizId, quizType);
        log.info("Alarm saved to database: {}", savedAlarm.getAlarmId());

        // 2. FCM 알림 전송
        String fcmResult = fcmNotificationService.sendQuizShareNotification(fromUser, toUser, quizId, quizType);
        log.info("FCM notification result: {}", fcmResult);

        return "Alarm saved with ID: " + savedAlarm.getAlarmId() + ". FCM result: " + fcmResult;
    }


    private String determineQuizType(String quizId) {
        if (quizId.startsWith("앱 퀴즈_")) {
            // Wqinfo에서 정확히 일치하는 제목이 있는지 확인
            List<Wqinfo> wqInfoList = wqinfoRepository.findByWqTitle(quizId);
            if (!wqInfoList.isEmpty()) {
                return "WQ";
            }
        }

        // Wqinfo에 없거나 "앱 퀴즈_"로 시작하지 않으면 SQ로 간주
        return "SQ";
    }

    private Alarm createAndSaveAlarm(User fromUser, User toUser, String content, String quizType) {
        Long nextSequence = alarmRepository.findMaxSequenceByUserId(toUser.getUid()) + 1;

        Alarm alarm = new Alarm();
        alarm.setFromUser(fromUser);
        alarm.setToUser(toUser);
        alarm.setContent(content);
        alarm.setIsRead(false);
        alarm.setCreateTime(LocalDateTime.now());
        alarm.setSequence(nextSequence);
        alarm.setQuizType(quizType);

        return alarmRepository.save(alarm);
    }

    // 최근 30개 알람 목록 반환
    public List<AlarmDTO> getAlarmList(String userId) {
        log.info("Fetching latest 30 alarms for user: {}", userId);
        PageRequest pageRequest = PageRequest.of(0, 30);
        List<Alarm> alarms = alarmRepository.findTop30ByToUserOrderByCreateTimeDesc(userId, pageRequest);
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
                alarm.getFromUser().getUid(),
                alarm.getToUser().getUid(),
                alarm.getQuizType()
        );
    }

    // 알람 상세
    public AlarmDetailDTO getAlarmById(String alarmId) {
        Alarm alarm = alarmRepository.findByIdWithFromUser(alarmId)
                .orElseThrow(() -> new AlarmNotFoundException("Alarm not found with id: " + alarmId));

        return convertToAlarmDetailDTO(alarm);
    }

    private AlarmDetailDTO convertToAlarmDetailDTO(Alarm alarm) {
        AlarmDetailDTO dto = new AlarmDetailDTO();
        dto.setAlarmId(alarm.getAlarmId());
        dto.setContent(alarm.getContent());
        dto.setFromUserName(alarm.getFromUser().getUName());
        dto.setQuizType(alarm.getQuizType());
        return dto;
    }

    // 알림 삭제
    public void deleteAlarm(String alarmId, String userId) {
        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new AlarmNotFoundException("Alarm not found with id: " + alarmId));

        if (!alarm.getToUser().getUid().equals(userId)) {
            throw new UnauthorizedException("User is not authorized to delete this alarm");
        }

        alarmRepository.delete(alarm);
    }
}