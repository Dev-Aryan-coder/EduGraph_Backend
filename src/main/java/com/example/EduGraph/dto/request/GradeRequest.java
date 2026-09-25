package com.example.EduGraph.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeRequest {
    @NotNull(message = "Drawing score is required")
    @Min(value = 0, message = "Drawing score cannot be negative")
    @Max(value = 10, message = "Drawing score cannot exceed 10")
    private Double drawingScore;

    private String teacherFeedback;
    private Boolean reject; // If true, sets status to REJECTED with feedback
}
