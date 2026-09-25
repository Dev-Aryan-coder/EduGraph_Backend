package com.example.EduGraph.controller;

import com.example.EduGraph.dto.response.ApiResponse;
import com.example.EduGraph.dto.response.FileUploadResponse;
import com.example.EduGraph.entity.UploadedFile;
import com.example.EduGraph.enums.FileType;
import com.example.EduGraph.security.SecurityUserPrincipal;
import com.example.EduGraph.service.FileStorageService;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")

public class FileUploadController {

    private final FileStorageService fileStorageService;

    public FileUploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "fileType", required = false) FileType fileType,
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        FileUploadResponse response = fileStorageService.storeFile(file, fileType, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "File uploaded successfully"));
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        UploadedFile metadata = fileStorageService.getFileMetadata(fileId);
        Resource resource = fileStorageService.loadFileAsResource(fileId);

        String contentType = metadata.getContentType() != null ? metadata.getContentType() : "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.getOriginalFileName() + "\"")
                .body(resource);
    }
}
