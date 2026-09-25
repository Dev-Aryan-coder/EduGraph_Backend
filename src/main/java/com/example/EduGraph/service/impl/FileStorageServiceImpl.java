package com.example.EduGraph.service.impl;

import com.example.EduGraph.dto.response.FileUploadResponse;
import com.example.EduGraph.entity.UploadedFile;
import com.example.EduGraph.entity.User;
import com.example.EduGraph.enums.FileType;
import com.example.EduGraph.exception.BadRequestException;
import com.example.EduGraph.exception.ResourceNotFoundException;
import com.example.EduGraph.repository.UploadedFileRepository;
import com.example.EduGraph.repository.UserRepository;
import com.example.EduGraph.service.FileStorageService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service

@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    private final UploadedFileRepository uploadedFileRepository;
    private final UserRepository userRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public FileStorageServiceImpl(UploadedFileRepository uploadedFileRepository, UserRepository userRepository) {
        this.uploadedFileRepository = uploadedFileRepository;
        this.userRepository = userRepository;
    }


    @Override
    @Transactional
    public FileUploadResponse storeFile(MultipartFile file, FileType fileType, Long uploaderId) {
        if (file.isEmpty()) {
            throw new BadRequestException("Uploaded file is empty");
        }

        User uploader = userRepository.findById(uploaderId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "file");
        String extension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFileName.substring(dotIndex);
        }

        String storedFileName = UUID.randomUUID().toString() + extension;

        try {
            Path targetLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(targetLocation)) {
                Files.createDirectories(targetLocation);
            }
            Path filePath = targetLocation.resolve(storedFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            UploadedFile uploadedFile = UploadedFile.builder()
                    .originalFileName(originalFileName)
                    .storedFileName(storedFileName)
                    .fileType(fileType != null ? fileType : FileType.DOCUMENT)
                    .sizeBytes(file.getSize())
                    .contentType(file.getContentType())
                    .storagePath(filePath.toString())
                    .fileUrl("/api/files/download/" + storedFileName)
                    .uploadedBy(uploader)
                    .build();

            UploadedFile saved = uploadedFileRepository.save(uploadedFile);

            String fileUrl = "/api/files/download/" + saved.getId();

            return FileUploadResponse.builder()
                    .id(saved.getId())
                    .originalFileName(saved.getOriginalFileName())
                    .storedFileName(saved.getStoredFileName())
                    .fileUrl(fileUrl)
                    .fileType(saved.getFileType())
                    .fileSize(saved.getSizeBytes())
                    .uploadedAt(saved.getCreatedAt())
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("Could not store file " + originalFileName + ". Please retry.", e);
        }
    }

    @Override
    public Resource loadFileAsResource(Long fileId) {
        UploadedFile metadata = getFileMetadata(fileId);
        try {
            Path filePath = Paths.get(metadata.getStoragePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found on disk: " + metadata.getOriginalFileName());
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File path invalid: " + metadata.getOriginalFileName(), ex);
        }
    }

    @Override
    public UploadedFile getFileMetadata(Long fileId) {
        return uploadedFileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File record not found with id: " + fileId));
    }
}