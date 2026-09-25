package com.example.EduGraph.controller;

import com.example.EduGraph.dto.request.ExcelImportConfirmRequest;
import com.example.EduGraph.dto.response.ApiResponse;
import com.example.EduGraph.dto.response.ExcelPreviewResponse;
import com.example.EduGraph.dto.response.PageResponse;
import com.example.EduGraph.dto.response.UserResponse;
import com.example.EduGraph.entity.User;
import com.example.EduGraph.enums.UserRole;
import com.example.EduGraph.exception.ResourceNotFoundException;
import com.example.EduGraph.mapper.EntityMapper;
import com.example.EduGraph.repository.UserRepository;
import com.example.EduGraph.security.SecurityUserPrincipal;
import com.example.EduGraph.service.EmailService;
import com.example.EduGraph.service.ExcelImportService;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/coordinator")

public class StaffOnboardingController {

    private final ExcelImportService excelImportService;
    private final UserRepository userRepository;
    private final EntityMapper mapper;
    private final EmailService emailService;

        public StaffOnboardingController(ExcelImportService excelImportService,
                                   UserRepository userRepository,
                                   EntityMapper mapper,
                                   EmailService emailService) {
        this.excelImportService = excelImportService;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.emailService = emailService;
    }


    @PostMapping("/import/students/preview")
    @PreAuthorize("hasRole('COORDINATOR')")
    public ResponseEntity<ApiResponse<ExcelPreviewResponse>> previewStudents(@RequestParam("file") MultipartFile file) {
        ExcelPreviewResponse preview = excelImportService.previewStudentImport(file);
        return ResponseEntity.ok(ApiResponse.success(preview, "Student sheet parsed in-memory"));
    }

    @PutMapping("/import/students/confirm")
    @PreAuthorize("hasRole('COORDINATOR')")
    public ResponseEntity<ApiResponse<Integer>> confirmStudents(
            @AuthenticationPrincipal SecurityUserPrincipal principal,
            @Valid @RequestBody ExcelImportConfirmRequest request) {
        int count = excelImportService.confirmStudentImport(principal.getCollegeId(), request);
        return ResponseEntity.ok(ApiResponse.success(count, "Successfully registered " + count + " students and dispatched credentials"));
    }

    @PostMapping("/import/teachers/preview")
    @PreAuthorize("hasRole('COORDINATOR')")
    public ResponseEntity<ApiResponse<ExcelPreviewResponse>> previewTeachers(@RequestParam("file") MultipartFile file) {
        ExcelPreviewResponse preview = excelImportService.previewTeacherImport(file);
        return ResponseEntity.ok(ApiResponse.success(preview, "Teacher sheet parsed in-memory"));
    }

    @PutMapping("/import/teachers/confirm")
    @PreAuthorize("hasRole('COORDINATOR')")
    public ResponseEntity<ApiResponse<Integer>> confirmTeachers(
            @AuthenticationPrincipal SecurityUserPrincipal principal,
            @Valid @RequestBody ExcelImportConfirmRequest request) {
        int count = excelImportService.confirmTeacherImport(principal.getCollegeId(), request);
        return ResponseEntity.ok(ApiResponse.success(count, "Successfully registered " + count + " teachers and dispatched credentials"));
    }

    @GetMapping("/students")
    @PreAuthorize("hasRole('COORDINATOR')")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getStudents(
            @AuthenticationPrincipal SecurityUserPrincipal principal,
            Pageable pageable) {
        Page<User> page = userRepository.findByCollegeIdAndRole(principal.getCollegeId(), UserRole.STUDENT, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(page.map(mapper::toUserResponse))));
    }

    @GetMapping("/teachers")
    @PreAuthorize("hasRole('COORDINATOR')")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getTeachers(
            @AuthenticationPrincipal SecurityUserPrincipal principal,
            Pageable pageable) {
        Page<User> page = userRepository.findByCollegeIdAndRole(principal.getCollegeId(), UserRole.TEACHER, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(page.map(mapper::toUserResponse))));
    }

    @PostMapping("/users/{id}/resend-credentials")
    @PreAuthorize("hasRole('COORDINATOR')")
    public ResponseEntity<ApiResponse<Void>> resendCredentials(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        emailService.sendCredentialsEmail(user.getEmail(), user.getFullName(), user.getPassword(), user.getRole().name());
        return ResponseEntity.ok(ApiResponse.success(null, "Credentials re-sent to " + user.getEmail()));
    }
}
