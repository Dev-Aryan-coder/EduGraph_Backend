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
public class TabSwitchLogResponse {
    private Long id;
    private LocalDateTime switchedAt;
    private String reason;
}
