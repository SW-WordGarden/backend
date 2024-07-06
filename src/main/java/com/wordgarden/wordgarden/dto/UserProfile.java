package com.wordgarden.wordgarden.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfile {
    private String uid;
    private String nickname;
    private int points;
    private int rank;

    public UserProfile(User user) {
        this.uid = user.getUid();
        this.nickname = user.getNickname();
        this.points = user.getPoints();
        this.rank = user.getRank();
    }

    public static class Settings {
        private boolean quizNotifications;
        private boolean subjectiveQuiz;

        // Getters and setters
        public boolean isQuizNotifications() {
            return quizNotifications;
        }

        public void setQuizNotifications(boolean quizNotifications) {
            this.quizNotifications = quizNotifications;
        }

        public boolean isSubjectiveQuiz() {
            return subjectiveQuiz;
        }

        public void setSubjectiveQuiz(boolean subjectiveQuiz) {
            this.subjectiveQuiz = subjectiveQuiz;
        }
    }
}
