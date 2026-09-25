package com.example.EduGraph.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TabSwitchEventRequest {
    private LocalDateTime timestamp;
    private String details;
}
