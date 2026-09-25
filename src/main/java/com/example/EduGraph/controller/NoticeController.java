package com.example.EduGraph.controller;

import com.example.EduGraph.dto.request.NoticeRequest;
import com.example.EduGraph.dto.response.ApiResponse;
import com.example.EduGraph.dto.response.NoticeResponse;
import com.example.EduGraph.security.SecurityUserPrincipal;
import com.example.EduGraph.service.NoticeService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")

public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_COORDINATOR', 'ROLE_TEACHER')")
    public ResponseEntity<ApiResponse<NoticeResponse>> createNotice(
            @Valid @RequestBody NoticeRequest request,
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        NoticeResponse response = noticeService.createNotice(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Notice broadcasted successfully"));
    }

    @GetMapping("/my-feed")
    public ResponseEntity<ApiResponse<List<NoticeResponse>>> getMyNotices(
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(noticeService.getNoticesForUser(principal.getId())));
    }

    @GetMapping("/college/{collegeId}")
    public ResponseEntity<ApiResponse<List<NoticeResponse>>> getNoticesByCollege(@PathVariable Long collegeId) {
        return ResponseEntity.ok(ApiResponse.success(noticeService.getNoticesByCollege(collegeId)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_COORDINATOR', 'ROLE_TEACHER')")
    public ResponseEntity<ApiResponse<Void>> deleteNotice(
            @PathVariable Long id,
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        noticeService.deleteNotice(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Notice removed"));
    }
}
