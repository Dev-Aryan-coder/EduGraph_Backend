package com.example.EduGraph.dto.request;

import com.example.EduGraph.enums.TicketType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequest {
    @NotNull(message = "Ticket type is required")
    private TicketType type;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description of requested change is required")
    private String description;

    private Long targetUserId;
    private String requestedChanges; // JSON string of proposed fields (e.g. corrected email, roll number, permission)
    private Long collegeId;
}
