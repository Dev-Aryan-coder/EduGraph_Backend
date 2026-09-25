package com.example.EduGraph.dto.response;

import com.example.EduGraph.enums.SubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResponse {
    private Long id;
    private Long assignmentId;
    private String assignmentTitle;
    private LocalDateTime assignmentDeadline;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private String studentRollNumber;
    private SubmissionStatus status;
    private String excalidrawDrawingData;
    private Integer tabSwitchCount;
    private Double mcqScore; // /20
    private Double drawingScore; // /10
    private Double totalScore; // /30
    private String teacherFeedback;
    private LocalDateTime submittedAt;
    private LocalDateTime gradedAt;
    private String gradedByName;
    private List<TabSwitchLogResponse> tabSwitchLogs;
}
