package com.example.EduGraph.dto.response;

import com.example.EduGraph.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeResponse {
    private Long id;
    private String title;
    private String content;
    private UserRole targetRole;
    private Long collegeId;
    private Long classroomId;
    private String classroomName;
    private Long authorId;
    private String authorName;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
