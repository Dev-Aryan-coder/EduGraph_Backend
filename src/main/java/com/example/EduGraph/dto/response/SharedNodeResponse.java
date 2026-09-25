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
public class SharedNodeResponse {

    private Long id;
    private Long nodeId;
    private String nodeTitle;
    private Long panelId;
    private String panelSubjectName;
    private Long sharedById;
    private String sharedByName;
    private String permission;
    private LocalDateTime createdAt;
}
