package com.wordgarden.wordgarden.config;

import com.wordgarden.wordgarden.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtRequestFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String userId = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            userId = JwtUtil.extractUserId(jwt);
        }

        if (userId != null && !JwtUtil.isTokenExpired(jwt)) {
            Claims claims = JwtUtil.extractClaims(jwt);
            request.setAttribute("claims", claims);
        }

        filterChain.doFilter(request, response);
    }
}
