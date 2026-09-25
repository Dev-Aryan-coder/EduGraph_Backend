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
public class NewsItemResponse {
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String sourceUrl;
    private String imageUrl;
    private String category;
    private Long authorId;
    private String authorName;
    private LocalDateTime createdAt;
}
