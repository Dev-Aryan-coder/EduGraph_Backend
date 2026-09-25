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
public class NodeResponse {

    private Long id;
    private Long panelId;
    private String subjectName;
    private String title;
    private String description;
    private String content;
    private Double positionX;
    private Double positionY;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
