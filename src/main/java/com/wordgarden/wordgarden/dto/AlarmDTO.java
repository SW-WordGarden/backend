package com.wordgarden.wordgarden.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AlarmDTO {
    private String alarmId;
    private String content;
    private Boolean isRead;
    private LocalDateTime createTime;
    private String fromUserUid;
    private String fromUserName;
    private String toUserName;
    private String quizType;
}