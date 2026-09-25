package com.example.EduGraph.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NodeConnectionResponse {

    private Long id;
    private Long sourceNodeId;
    private String sourceNodeTitle;
    private Long targetNodeId;
    private String targetNodeTitle;
    private String label;
}
