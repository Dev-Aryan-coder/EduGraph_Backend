package com.example.EduGraph.service;

import com.example.EduGraph.dto.request.CoordinatorCreateRequest;
import com.example.EduGraph.dto.request.PrincipalRegisterRequest;
import com.example.EduGraph.dto.response.CollegeOverviewResponse;
import com.example.EduGraph.dto.response.LoginResponse;
import com.example.EduGraph.dto.response.UserResponse;

import java.util.List;

public interface CollegeService {

    LoginResponse registerPrincipalAndCollege(PrincipalRegisterRequest request);

    UserResponse createCoordinator(Long principalCollegeId, CoordinatorCreateRequest request);

    List<UserResponse> getCoordinators(Long collegeId);

    CollegeOverviewResponse getCollegeOverview(Long collegeId);
}
