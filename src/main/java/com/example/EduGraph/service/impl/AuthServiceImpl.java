package com.example.EduGraph.service.impl;

import com.example.EduGraph.dto.request.LoginRequest;
import com.example.EduGraph.dto.request.RefreshRequest;
import com.example.EduGraph.dto.response.LoginResponse;
import com.example.EduGraph.entity.User;
import com.example.EduGraph.enums.AccountStatus;
import com.example.EduGraph.exception.UnauthorizedAccessException;
import com.example.EduGraph.repository.UserRepository;
import com.example.EduGraph.security.JwtUtil;
import com.example.EduGraph.service.AuthService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service

public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmailAndStatus(request.getEmail(), AccountStatus.ACTIVE)
                .orElseThrow(() -> new UnauthorizedAccessException("Account not found. Please contact your College Coordinator or Principal."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()) && !request.getPassword().equals(user.getPassword())) {
            throw new UnauthorizedAccessException("Invalid email or password.");
        }

        Long collegeId = user.getCollege() != null ? user.getCollege().getId() : null;
        String collegeName = user.getCollege() != null ? user.getCollege().getName() : null;
        Long classroomId = user.getClassroom() != null ? user.getClassroom().getId() : null;
        String classroomName = user.getClassroom() != null ? user.getClassroom().getName() : null;

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name(), collegeId, classroomId);

        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .collegeId(collegeId)
                .collegeName(collegeName)
                .classroomId(classroomId)
                .classroomName(classroomName)
                .build();
    }

    @Override
    public LoginResponse refreshToken(RefreshRequest request) {
        String token = request.getRefreshToken();
        if (jwtUtil.isTokenExpired(token)) {
            throw new UnauthorizedAccessException("Refresh token expired. Please log in again.");
        }

        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmailAndStatus(email, AccountStatus.ACTIVE)
                .orElseThrow(() -> new UnauthorizedAccessException("Account inactive or no longer found."));

        Long collegeId = user.getCollege() != null ? user.getCollege().getId() : null;
        String collegeName = user.getCollege() != null ? user.getCollege().getName() : null;
        Long classroomId = user.getClassroom() != null ? user.getClassroom().getId() : null;
        String classroomName = user.getClassroom() != null ? user.getClassroom().getName() : null;

        String newToken = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name(), collegeId, classroomId);

        return LoginResponse.builder()
                .token(newToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .collegeId(collegeId)
                .collegeName(collegeName)
                .classroomId(classroomId)
                .classroomName(classroomName)
                .build();
    }
}
