package com.example.EduGraph.dto.request;

import com.example.EduGraph.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeRequest {
    @NotBlank(message = "Notice title is required")
    private String title;

    @NotBlank(message = "Notice content is required")
    private String content;

    private UserRole targetRole; // null for broadcast to all
    private Long classroomId;    // null for entire college
    private Long collegeId;
    private LocalDateTime expiresAt;
}
