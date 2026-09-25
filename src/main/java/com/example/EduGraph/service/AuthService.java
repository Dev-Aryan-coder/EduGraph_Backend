package com.example.EduGraph.service;

import com.example.EduGraph.dto.request.LoginRequest;
import com.example.EduGraph.dto.request.RefreshRequest;
import com.example.EduGraph.dto.request.ResetPasswordRequest;
import com.example.EduGraph.dto.request.SendOtpRequest;
import com.example.EduGraph.dto.request.VerifyOtpRequest;
import com.example.EduGraph.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    LoginResponse refreshToken(RefreshRequest request);

    void sendOtp(SendOtpRequest request);

    void verifyOtp(VerifyOtpRequest request);

    void resetPassword(ResetPasswordRequest request);
}