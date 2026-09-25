package com.example.EduGraph.dto.response;

import com.example.EduGraph.enums.TicketStatus;
import com.example.EduGraph.enums.TicketType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {
    private Long id;
    private TicketType type;
    private TicketStatus status;
    private String title;
    private String description;
    private Long createdById;
    private String createdByName;
    private String createdByEmail;
    private Long targetUserId;
    private String targetUserName;
    private String requestedChanges;
    private Long assignedToId;
    private String assignedToName;
    private String resolutionNotes;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
