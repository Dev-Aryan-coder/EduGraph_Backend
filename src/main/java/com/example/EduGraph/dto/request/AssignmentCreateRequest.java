package com.example.EduGraph.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class AssignmentCreateRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Classroom ID is required")
    private Long classroomId;

    private String subject;

    @NotNull(message = "Deadline is required")
    private LocalDateTime deadline;

    private String excalidrawTemplateData;

    @NotEmpty(message = "Assignment must have 20 MCQ questions")
    private List<MCQQuestionRequest> questions;
}
