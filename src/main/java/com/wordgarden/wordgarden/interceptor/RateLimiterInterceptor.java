package com.wordgarden.wordgarden.interceptor;

import io.github.bucket4j.Bucket;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public class RateLimiterInterceptor implements HandlerInterceptor {
    private final Map<String, Bucket>  buckets;
    public RateLimiterInterceptor(Map<String, Bucket> buckets){
        this.buckets = buckets;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        Bucket bucket = buckets.get(path.split("/")[1]);

        if (bucket == null || bucket.tryConsume(1)) {
            return true;
        }
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.getWriter().write("너무 많은 요청이 발생했습니다. 잠시 후 다시 시도해주세요.");
        return false;
    }
}
