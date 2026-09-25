package com.example.EduGraph.service;

import com.example.EduGraph.dto.response.FileUploadResponse;
import com.example.EduGraph.entity.UploadedFile;
import com.example.EduGraph.enums.FileType;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    FileUploadResponse storeFile(MultipartFile file, FileType fileType, Long uploaderId);
    Resource loadFileAsResource(Long fileId);
    UploadedFile getFileMetadata(Long fileId);
}
