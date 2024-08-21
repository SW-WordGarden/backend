package com.wordgarden.wordgarden.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.wordgarden.wordgarden.entity.Alarm;
import com.wordgarden.wordgarden.entity.User;
import com.wordgarden.wordgarden.repository.AlarmRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FCMNotificationService {
    private static final Logger log = LoggerFactory.getLogger(FCMNotificationService.class);

    @Autowired
    private FCMTokenService fcmTokenService;

    public String sendQuizShareNotification(User fromUser, User toUser, String quizTitle, String quizType) {
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
            log.info("Successfully sent FCM message: {}", response);
            return "FCM sent successfully: " + response;
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send FCM message", e);
            return "Failed to send FCM message: " + e.getMessage();
        }
    }
}