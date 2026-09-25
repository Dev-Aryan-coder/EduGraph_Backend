package com.example.EduGraph.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardStatsResponse {
    private Long totalColleges;
    private Long totalClassrooms;
    private Long totalUsers;
    private Long totalStudents;
    private Long totalTeachers;
    private Long totalCoordinators;
    private Long totalAssignments;
    private Long totalSubmissions;
    private Long pendingTicketsCount;
    private Long activeNoticesCount;
}
