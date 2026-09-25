package com.example.EduGraph.service.impl;

import com.example.EduGraph.dto.request.LoginRequest;
import com.example.EduGraph.dto.request.RefreshRequest;
import com.example.EduGraph.dto.request.ResetPasswordRequest;
import com.example.EduGraph.dto.request.SendOtpRequest;
import com.example.EduGraph.dto.request.VerifyOtpRequest;
import com.example.EduGraph.dto.response.LoginResponse;
import com.example.EduGraph.entity.PasswordResetOtp;
import com.example.EduGraph.entity.User;
import com.example.EduGraph.enums.AccountStatus;
import com.example.EduGraph.exception.BadRequestException;
import com.example.EduGraph.exception.ResourceNotFoundException;
import com.example.EduGraph.exception.UnauthorizedAccessException;
import com.example.EduGraph.repository.PasswordResetOtpRepository;
import com.example.EduGraph.repository.UserRepository;
import com.example.EduGraph.security.JwtUtil;
import com.example.EduGraph.service.AuthService;
import com.example.EduGraph.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetOtpRepository otpRepository;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthServiceImpl(UserRepository userRepository,
                           JwtUtil jwtUtil,
                           PasswordEncoder passwordEncoder,
                           PasswordResetOtpRepository otpRepository,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.otpRepository = otpRepository;
        this.emailService = emailService;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmailAndStatus(request.getEmail().trim().toLowerCase(), AccountStatus.ACTIVE)
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

    @Override
    @Transactional
    public void sendOtp(SendOtpRequest request) {
        String cleanEmail = request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new ResourceNotFoundException("No account registered with email: " + request.getEmail()));

        if (user.getStatus() != AccountStatus.ACTIVE) {
            throw new UnauthorizedAccessException("Account is currently inactive. Please contact your administrator.");
        }

        // Generate 6-digit numeric OTP
        int number = secureRandom.nextInt(900000) + 100000;
        String otpCode = String.valueOf(number);

        PasswordResetOtp resetOtp = PasswordResetOtp.builder()
                .email(cleanEmail)
                .otp(otpCode)
                .expiryTime(LocalDateTime.now().plusMinutes(10))
                .used(false)
                .build();
        otpRepository.save(resetOtp);

        log.info("Dispatching password reset OTP to email {}", cleanEmail);
        emailService.sendOtpEmail(user.getEmail(), user.getFullName(), otpCode);
    }

    @Override
    @Transactional(readOnly = true)
    public void verifyOtp(VerifyOtpRequest request) {
        String cleanEmail = request.getEmail().trim().toLowerCase();
        String cleanOtp = request.getOtp().trim();

        PasswordResetOtp resetOtp = otpRepository.findTopByEmailAndUsedFalseOrderByCreatedAtDesc(cleanEmail)
                .orElseThrow(() -> new BadRequestException("No active OTP request found for this email. Please request a new code."));

        if (resetOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Verification code has expired. Please request a new one.");
        }

        if (!resetOtp.getOtp().equals(cleanOtp)) {
            throw new BadRequestException("Invalid verification code. Please check and try again.");
        }
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String cleanEmail = request.getEmail().trim().toLowerCase();
        String cleanOtp = request.getOtp().trim();

        PasswordResetOtp resetOtp = otpRepository.findTopByEmailAndUsedFalseOrderByCreatedAtDesc(cleanEmail)
                .orElseThrow(() -> new BadRequestException("No active OTP request found. Please start over."));

        if (resetOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Verification code has expired. Please request a new one.");
        }

        if (!resetOtp.getOtp().equals(cleanOtp)) {
            throw new BadRequestException("Invalid verification code.");
        }

        User user = userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword().trim()));
        userRepository.save(user);

        resetOtp.setUsed(true);
        otpRepository.save(resetOtp);

        log.info("Password successfully updated for user {}", cleanEmail);
    }
}