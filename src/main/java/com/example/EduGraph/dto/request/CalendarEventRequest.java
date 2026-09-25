package com.example.EduGraph.dto.request;

import com.example.EduGraph.enums.CalendarEventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEventRequest {
    @NotBlank(message = "Event title is required")
    private String title;

    private String description;

    @NotNull(message = "Event type is required")
    private CalendarEventType eventType;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;
    private Long collegeId;
    private Boolean isHoliday;
}
