package com.example.EduGraph.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResearchDocumentResponse {
    private Long id;
    private String title;
    private String abstractText;
    private String authors;
    private String journalOrConference;
    private LocalDate publicationDate;
    private String documentFileUrl;
    private Long uploadedById;
    private String uploadedByName;
    private LocalDateTime createdAt;
}
