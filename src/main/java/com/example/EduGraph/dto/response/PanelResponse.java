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
public class PanelResponse {

    private Long id;
    private String subjectName;
    private Long studentId;
    private String studentName;
    private int nodeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
