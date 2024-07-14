package com.wordgarden.wordgarden.scheduler;

import com.wordgarden.wordgarden.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class ScheduledTasks {
    @Autowired
    private WordService wordService;

    @Scheduled(cron = "0 0 0 */7 * ?")
    public void updateLearningWords() {
        wordService.updateLearningWords();
    }

    @Scheduled(cron = "0 0 1 */7 * ?")
    public void cleanUpWeeklyWords() {
        wordService.cleanUpWeeklyWords();
    }
}
