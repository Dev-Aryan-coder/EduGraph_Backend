package com.example.EduGraph.controller;

import com.example.EduGraph.dto.request.AssignmentCreateRequest;
import com.example.EduGraph.dto.request.ExtendDeadlineRequest;
import com.example.EduGraph.dto.response.ApiResponse;
import com.example.EduGraph.dto.response.AssignmentResponse;
import com.example.EduGraph.dto.response.AssignmentStatsResponse;
import com.example.EduGraph.security.SecurityUserPrincipal;
import com.example.EduGraph.service.AssignmentService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")

public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_PRINCIPAL')")
    public ResponseEntity<ApiResponse<AssignmentResponse>> createAssignment(
            @Valid @RequestBody AssignmentCreateRequest request,
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        AssignmentResponse response = assignmentService.createAssignment(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Assignment created with 20 MCQ verification questions successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AssignmentResponse>> getAssignmentById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(assignmentService.getAssignmentById(id)));
    }

    @GetMapping("/classroom/{classroomId}")
    public ResponseEntity<ApiResponse<List<AssignmentResponse>>> getAssignmentsByClassroom(@PathVariable Long classroomId) {
        return ResponseEntity.ok(ApiResponse.success(assignmentService.getAssignmentsByClassroom(classroomId)));
    }

    @GetMapping("/student/my-assignments")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ApiResponse<List<AssignmentResponse>>> getMyAssignments(
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(assignmentService.getAssignmentsForStudent(principal.getId())));
    }

    @PostMapping("/{id}/extend-deadline")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_PRINCIPAL')")
    public ResponseEntity<ApiResponse<AssignmentResponse>> extendDeadline(
            @PathVariable Long id,
            @Valid @RequestBody ExtendDeadlineRequest request,
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        AssignmentResponse response = assignmentService.extendDeadline(id, request, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Deadline extended successfully by " + request.getExtensionHours() + " hours"));
    }

    @GetMapping("/{id}/stats")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_PRINCIPAL')")
    public ResponseEntity<ApiResponse<AssignmentStatsResponse>> getAssignmentStats(
            @PathVariable Long id,
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(assignmentService.getAssignmentStats(id, principal.getId())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAssignment(
            @PathVariable Long id,
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        assignmentService.deleteAssignment(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Assignment removed successfully"));
    }
}
