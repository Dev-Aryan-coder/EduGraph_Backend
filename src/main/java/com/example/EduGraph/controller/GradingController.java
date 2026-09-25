package com.example.EduGraph.controller;

import com.example.EduGraph.dto.request.GradeRequest;
import com.example.EduGraph.dto.response.ApiResponse;
import com.example.EduGraph.dto.response.SubmissionResponse;
import com.example.EduGraph.security.SecurityUserPrincipal;
import com.example.EduGraph.service.GradingService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/grading")

public class GradingController {

    private final GradingService gradingService;

    public GradingController(GradingService gradingService) {
        this.gradingService = gradingService;
    }

    @PostMapping("/submission/{submissionId}")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_PRINCIPAL')")
    public ResponseEntity<ApiResponse<SubmissionResponse>> gradeSubmission(
            @PathVariable Long submissionId,
            @Valid @RequestBody GradeRequest request,
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        SubmissionResponse response = gradingService.gradeSubmission(submissionId, request, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Submission graded and notification emailed to student"));
    }
}
