package com.wordgarden.wordgarden.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AlarmDTO {
    private Long alarmId;
    private String content;
    private Boolean isRead;
    private LocalDateTime createTime;
    private String fromUserName;
    private String toUserName;
}