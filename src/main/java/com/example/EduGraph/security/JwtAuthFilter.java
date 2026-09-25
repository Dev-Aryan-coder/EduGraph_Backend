package com.example.EduGraph.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            if (!jwtUtil.isTokenExpired(token)) {
                Claims claims = jwtUtil.extractAllClaims(token);
                String email = claims.getSubject();
                String role = (String) claims.get("role");
                Long userId = jwtUtil.extractUserId(token);
                Long collegeId = jwtUtil.extractCollegeId(token);
                Long classroomId = claims.get("classroomId") instanceof Number ? ((Number) claims.get("classroomId")).longValue() : null;

                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                SecurityUserPrincipal principal = SecurityUserPrincipal.builder()
                        .id(userId)
                        .email(email)
                        .role(role)
                        .collegeId(collegeId)
                        .classroomId(classroomId)
                        .authorities(Collections.singletonList(authority))
                        .build();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.warn("JWT authentication failed: " + ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
