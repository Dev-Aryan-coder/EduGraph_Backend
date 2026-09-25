package com.example.EduGraph.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentStatsResponse {
    private Long assignmentId;
    private String assignmentTitle;
    private Integer totalStudents;
    private Integer submittedCount;
    private Integer gradedCount;
    private Integer pendingCount;
    private Double averageScore;
    private Integer flaggedTabSwitchCount; // students with > 3 tab switches
}
