package com.wordgarden.wordgarden.scheduler;

import com.wordgarden.wordgarden.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class ScheduledTasks {
    @Autowired
    private WordService wordService;

    // 주간학습 단어 초기화
    @Scheduled(cron = "0 0 0 */7 * ?")
    public void updateLearningWords() {
        wordService.updateLearningWords();
    }

    // 주간 테스트 단어 초기화
    @Scheduled(cron = "0 0 1 */7 * ?")
    public void cleanUpWeeklyWords() {
        wordService.cleanUpWeeklyWords();
    }

    // 일일 단어 테스트 퀴즈 참여 횟수 초기화

}
