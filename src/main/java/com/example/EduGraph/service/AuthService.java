package com.example.EduGraph.service;

import com.example.EduGraph.dto.request.LoginRequest;
import com.example.EduGraph.dto.request.RefreshRequest;
import com.example.EduGraph.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    LoginResponse refreshToken(RefreshRequest request);
}
