package com.example.EduGraph.security;

import com.example.EduGraph.dto.response.ApiResponse;
import com.example.EduGraph.dto.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ErrorResponse errorData = ErrorResponse.builder()
                .errorCode("UNAUTHORIZED")
                .details(authException.getMessage() != null ? authException.getMessage() : "Full authentication is required to access this resource")
                .build();

        ApiResponse<ErrorResponse> apiResponse = ApiResponse.error("Unauthorized access", errorData);

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
