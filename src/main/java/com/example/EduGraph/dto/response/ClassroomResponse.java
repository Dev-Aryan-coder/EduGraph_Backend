package com.example.EduGraph.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassroomResponse {
    private Long id;
    private String name;
    private String section;
    private String academicYear;
    private Long collegeId;
    private String collegeName;
    private Long teacherId;
    private String teacherName;
    private Long coordinatorId;
    private String coordinatorName;
    private int studentCount;
    private LocalDateTime createdAt;
}