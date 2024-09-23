package com.wordgarden.wordgarden.scheduler;

import com.wordgarden.wordgarden.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class ScheduledTasks {
    @Autowired
    private WordService wordService;

    @Bean
    public WebClient webClient() {
        return WebClient.create();
    }

    @Autowired
    private WebClient webClient;

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

    // 30분마다 Keep-Alive API 호출
    @Scheduled(fixedRate = 1800000)  // 30분마다 실행
    public void keepAliveTask() {
        String keepAliveUrl = "http://localhost:8080/login/keep-alive";  // 실제 API 주소
        webClient.get()
                .uri(keepAliveUrl)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> System.out.println("Keep-Alive 요청이 전송되었습니다."))
                .doOnError(error -> System.err.println("Keep-Alive 요청 실패: " + error.getMessage()))
                .onErrorResume(error -> Mono.empty())
                .subscribe();
    }
}