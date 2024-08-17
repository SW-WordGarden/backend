package com.wordgarden.wordgarden.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.wordgarden.wordgarden.entity.Alarm;
import com.wordgarden.wordgarden.entity.User;
import com.wordgarden.wordgarden.repository.AlarmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FCMNotificationService {
    @Autowired
    private FCMTokenService fcmTokenService;

    @Autowired
    private AlarmRepository alarmRepository;

    public void sendQuizShareNotification(User fromUser, User toUser, String quizTitle, String quizType) {
        String token = fcmTokenService.getUserFCMToken(toUser.getUid());

        String title = "New Quiz Shared";
        String body = fromUser.getUName() + " shared a " + quizType + " quiz with you: " + quizTitle;

        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setToken(token)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);

            // Save to database
            Alarm alarm = new Alarm();
            alarm.setContent(quizTitle);
            alarm.setIsRead(false);
            alarm.setCreateTime(LocalDateTime.now());
            alarm.setFromUser(fromUser);
            alarm.setToUser(toUser);
            alarmRepository.save(alarm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
