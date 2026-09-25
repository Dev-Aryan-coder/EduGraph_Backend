package com.example.EduGraph.dto.response;

import com.example.EduGraph.enums.CalendarEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEventResponse {
    private Long id;
    private String title;
    private String description;
    private CalendarEventType eventType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long collegeId;
    private String collegeName;
    private Boolean isHoliday;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
}
