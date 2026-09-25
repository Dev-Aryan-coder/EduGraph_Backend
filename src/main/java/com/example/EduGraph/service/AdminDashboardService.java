package com.example.EduGraph.service;

import com.example.EduGraph.dto.response.AdminDashboardStatsResponse;

public interface AdminDashboardService {
    AdminDashboardStatsResponse getDashboardStats(Long adminId);
}
