package com.example.EduGraph.dto.request;

import com.example.EduGraph.enums.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResolutionRequest {
    @NotNull(message = "Resolution status is required")
    private TicketStatus status; // APPROVED, REJECTED, RESOLVED

    private String resolutionNotes;
}
