package com.example.EduGraph.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentResponse {
    private Long id;
    private String title;
    private String description;
    private Long classroomId;
    private String classroomName;
    private Long teacherId;
    private String teacherName;
    private String subject;
    private LocalDateTime deadline;
    private String excalidrawTemplateData;
    private Integer extensionGrantedHours;
    private String extensionReason;
    private Integer mcqCount;
    private LocalDateTime createdAt;
    private Boolean isExpired;
}
