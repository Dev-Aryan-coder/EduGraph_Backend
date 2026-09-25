package com.example.EduGraph.dto.response;

import com.example.EduGraph.enums.FileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private Long id;
    private String originalFileName;
    private String storedFileName;
    private String fileUrl;
    private FileType fileType;
    private Long fileSize;
    private LocalDateTime uploadedAt;
}
