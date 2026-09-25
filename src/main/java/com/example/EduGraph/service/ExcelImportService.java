package com.example.EduGraph.service;

import com.example.EduGraph.dto.request.ExcelImportConfirmRequest;
import com.example.EduGraph.dto.response.ExcelPreviewResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ExcelImportService {

    ExcelPreviewResponse previewStudentImport(MultipartFile file);

    int confirmStudentImport(Long collegeId, ExcelImportConfirmRequest request);

    ExcelPreviewResponse previewTeacherImport(MultipartFile file);

    int confirmTeacherImport(Long collegeId, ExcelImportConfirmRequest request);
}
