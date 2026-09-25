package com.example.EduGraph.controller;

import com.example.EduGraph.dto.response.AdminDashboardStatsResponse;
import com.example.EduGraph.dto.response.ApiResponse;
import com.example.EduGraph.security.SecurityUserPrincipal;
import com.example.EduGraph.service.AdminDashboardService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")

public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PRINCIPAL')")
    public ResponseEntity<ApiResponse<AdminDashboardStatsResponse>> getStats(
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(adminDashboardService.getDashboardStats(principal.getId())));
    }
}
