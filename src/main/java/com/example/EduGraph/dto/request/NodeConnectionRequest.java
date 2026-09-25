package com.example.EduGraph.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NodeConnectionRequest {

    @NotNull(message = "Target node ID is required")
    private Long targetNodeId;

    private String label;
}
