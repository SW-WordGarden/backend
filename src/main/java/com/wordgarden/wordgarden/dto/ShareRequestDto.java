package com.wordgarden.wordgarden.dto;

import lombok.Data;

@Data
public class ShareRequestDto {
    private String fromUserId;
    private String toUserId;
    private String quizId;

    // 기본 생성자
    public ShareRequestDto() {}

    // 모든 필드를 포함한 생성자
    public ShareRequestDto(String fromUserId, String toUserId, String quizId) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.quizId = quizId;
    }

    // toString 메소드 (디버깅 용도)
    @Override
    public String toString() {
        return "QuizShareRequest{" +
                "fromUserId='" + fromUserId + '\'' +
                ", toUserId='" + toUserId + '\'' +
                ", quizId='" + quizId + '\'' +
                '}';
    }
}
