package com.example.EduGraph.controller;

import com.example.EduGraph.dto.request.MCQAnswerRequest;
import com.example.EduGraph.dto.response.ApiResponse;
import com.example.EduGraph.dto.response.MCQQuestionResponse;
import com.example.EduGraph.dto.response.MCQResultResponse;
import com.example.EduGraph.security.SecurityUserPrincipal;
import com.example.EduGraph.service.MCQService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mcq")

public class MCQController {

    private final MCQService mcqService;

    public MCQController(MCQService mcqService) {
        this.mcqService = mcqService;
    }

    @GetMapping("/assignment/{assignmentId}/questions")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ApiResponse<List<MCQQuestionResponse>>> getQuestionsForStudent(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(mcqService.getQuestionsForStudent(assignmentId, principal.getId())));
    }

    @GetMapping("/assignment/{assignmentId}/teacher-view")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_PRINCIPAL')")
    public ResponseEntity<ApiResponse<List<MCQQuestionResponse>>> getQuestionsForTeacher(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(mcqService.getQuestionsForTeacher(assignmentId, principal.getId())));
    }

    @PostMapping("/assignment/{assignmentId}/submit")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ApiResponse<MCQResultResponse>> submitMCQ(
            @PathVariable Long assignmentId,
            @Valid @RequestBody List<MCQAnswerRequest> answers,
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        MCQResultResponse result = mcqService.submitMCQAnswers(assignmentId, principal.getId(), answers);
        return ResponseEntity.ok(ApiResponse.success(result, "MCQ assessment evaluated successfully"));
    }

    @GetMapping("/assignment/{assignmentId}/my-result")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ApiResponse<MCQResultResponse>> getMyMCQResult(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(mcqService.getStudentMCQResult(assignmentId, principal.getId())));
    }
}
