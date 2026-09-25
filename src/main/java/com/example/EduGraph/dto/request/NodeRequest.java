package com.example.EduGraph.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class NodeRequest {

    @NotBlank(message = "Node title is required")
    private String title;

    private String description;

    private String content;

    private Double positionX;

    private Double positionY;
}
