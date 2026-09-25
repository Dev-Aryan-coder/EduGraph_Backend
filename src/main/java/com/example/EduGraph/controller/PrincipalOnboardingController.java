package com.example.EduGraph.controller;

import com.example.EduGraph.dto.request.CoordinatorCreateRequest;
import com.example.EduGraph.dto.request.PrincipalRegisterRequest;
import com.example.EduGraph.dto.response.ApiResponse;
import com.example.EduGraph.dto.response.ClassroomResponse;
import com.example.EduGraph.dto.response.CollegeOverviewResponse;
import com.example.EduGraph.dto.response.LoginResponse;
import com.example.EduGraph.dto.response.UserResponse;
import com.example.EduGraph.security.SecurityUserPrincipal;
import com.example.EduGraph.service.CollegeService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/principal")

public class PrincipalOnboardingController {

    private final CollegeService collegeService;

    public PrincipalOnboardingController(CollegeService collegeService) {
        this.collegeService = collegeService;
    }


    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> registerCollegeAndPrincipal(
            @Valid @RequestBody PrincipalRegisterRequest request) {
        LoginResponse response = collegeService.registerPrincipalAndCollege(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "College and Principal registered successfully"));
    }

    @PostMapping("/coordinators")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse<UserResponse>> createCoordinator(
            @AuthenticationPrincipal SecurityUserPrincipal principal,
            @Valid @RequestBody CoordinatorCreateRequest request) {
        UserResponse response = collegeService.createCoordinator(principal.getCollegeId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Coordinator created and credentials dispatched via email"));
    }

    @GetMapping("/coordinators")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getCoordinators(
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        List<UserResponse> list = collegeService.getCoordinators(principal.getCollegeId());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/teachers")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getTeachers(
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        List<UserResponse> list = collegeService.getTeachers(principal.getCollegeId());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/classrooms")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse<List<ClassroomResponse>>> getClassrooms(
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        List<ClassroomResponse> list = collegeService.getClassrooms(principal.getCollegeId());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/overview")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse<CollegeOverviewResponse>> getOverview(
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        CollegeOverviewResponse overview = collegeService.getCollegeOverview(principal.getCollegeId());
        return ResponseEntity.ok(ApiResponse.success(overview));
    }
}
