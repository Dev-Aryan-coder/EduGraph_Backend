package com.example.EduGraph.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtendDeadlineRequest {
    @NotNull(message = "Extension hours is required")
    @Min(value = 1, message = "Extension must be at least 1 hour")
    @Max(value = 48, message = "Deadline extension cannot exceed 48 hours (2 days)")
    private Integer extensionHours;

    @NotBlank(message = "Reason for extension is mandatory")
    private String reason;
}
