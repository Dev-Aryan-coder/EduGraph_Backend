package com.example.EduGraph.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollegeOverviewResponse {

    private Long collegeId;
    private String collegeName;
    private long totalCoordinators;
    private long totalTeachers;
    private long totalStudents;
    private long totalClassrooms;
}
