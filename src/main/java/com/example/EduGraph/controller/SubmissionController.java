package com.example.EduGraph.controller;

import com.example.EduGraph.dto.request.SubmissionContentRequest;
import com.example.EduGraph.dto.request.TabSwitchEventRequest;
import com.example.EduGraph.dto.response.ApiResponse;
import com.example.EduGraph.dto.response.SubmissionResponse;
import com.example.EduGraph.security.SecurityUserPrincipal;
import com.example.EduGraph.service.SubmissionService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")

public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping("/assignment/{assignmentId}/tab-switch")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ApiResponse<Void>> recordTabSwitch(
            @PathVariable Long assignmentId,
            @RequestBody TabSwitchEventRequest request,
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        submissionService.recordTabSwitch(assignmentId, principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.<Void>success(null, "Tab switch recorded for proctoring audit"));
    }

    @PostMapping("/assignment/{assignmentId}/draft")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ApiResponse<SubmissionResponse>> saveDraft(
            @PathVariable Long assignmentId,
            @RequestBody SubmissionContentRequest request,
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        SubmissionResponse response = submissionService.saveDrawingDraft(assignmentId, principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "Whiteboard drawing draft saved"));
    }

    @PostMapping("/assignment/{assignmentId}/finalize")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ApiResponse<SubmissionResponse>> finalizeSubmission(
            @PathVariable Long assignmentId,
            @RequestBody SubmissionContentRequest request,
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        SubmissionResponse response = submissionService.finalizeSubmission(assignmentId, principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "Assignment submitted successfully! Email confirmation sent."));
    }

    @GetMapping("/assignment/{assignmentId}/my-submission")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ApiResponse<SubmissionResponse>> getMySubmission(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(submissionService.getStudentSubmission(assignmentId, principal.getId())));
    }

    @GetMapping("/assignment/{assignmentId}/all")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_PRINCIPAL')")
    public ResponseEntity<ApiResponse<List<SubmissionResponse>>> getSubmissionsForAssignment(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(submissionService.getSubmissionsForAssignment(assignmentId, principal.getId())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubmissionResponse>> getSubmissionById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(submissionService.getSubmissionById(id)));
    }
}