package com.wordgarden.wordgarden.config;

import com.wordgarden.wordgarden.interceptor.RateLimiterInterceptor;
import io.github.bucket4j.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import java.util.*;
import java.time.Duration;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        Map<String, Bucket> buckets = new HashMap<>();

        // 분당 호출 횟수 제한
        buckets.put("word", createBucket(300, Duration.ofMinutes(1)));
        buckets.put("like", createBucket(300, Duration.ofMinutes(1)));
        buckets.put("wq", createBucket(300, Duration.ofMinutes(1)));
        buckets.put("sq", createBucket(500, Duration.ofMinutes(1)));
        buckets.put("user", createBucket(200, Duration.ofMinutes(1)));
        buckets.put("share", createBucket(200, Duration.ofMinutes(1)));
        buckets.put("garden", createBucket(500, Duration.ofMinutes(1)));
        buckets.put("onequiz", createBucket(10000, Duration.ofMinutes(1)));

        registry.addInterceptor(new RateLimiterInterceptor(buckets))
                .addPathPatterns("/**") // 모든 경로에 적용
                .excludePathPatterns("/error", "/favicon.ico"); // 필요한 경로 제외
    }

    private Bucket createBucket(int capacity, Duration duration) {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(capacity, Refill.greedy(capacity, duration)))
                .build();
    }
}
