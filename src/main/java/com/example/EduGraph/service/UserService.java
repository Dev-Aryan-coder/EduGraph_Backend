package com.example.EduGraph.service;

import com.example.EduGraph.dto.request.PasswordChangeRequest;
import com.example.EduGraph.dto.request.ProfileUpdateRequest;
import com.example.EduGraph.dto.response.UserResponse;

public interface UserService {

    UserResponse getCurrentUser(Long userId);

    UserResponse updateProfile(Long userId, ProfileUpdateRequest request);

    void changePassword(Long userId, PasswordChangeRequest request);
}
