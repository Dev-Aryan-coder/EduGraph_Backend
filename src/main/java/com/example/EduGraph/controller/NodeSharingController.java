package com.example.EduGraph.controller;

import com.example.EduGraph.dto.request.ShareRequest;
import com.example.EduGraph.dto.response.ApiResponse;
import com.example.EduGraph.dto.response.SharedNodeResponse;
import com.example.EduGraph.security.SecurityUserPrincipal;
import com.example.EduGraph.service.NodeSharingService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController

public class NodeSharingController {

    private final NodeSharingService sharingService;

    public NodeSharingController(NodeSharingService nodeSharingService) {
        this.sharingService = nodeSharingService;
    }


    @PostMapping("/api/nodes/{id}/share")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<SharedNodeResponse>> shareNode(
            @AuthenticationPrincipal SecurityUserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody ShareRequest request) {
        SharedNodeResponse response = sharingService.shareNode(id, principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Topic node shared in view-only mode"));
    }

    @PostMapping("/api/panels/{id}/share")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<SharedNodeResponse>> sharePanel(
            @AuthenticationPrincipal SecurityUserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody ShareRequest request) {
        SharedNodeResponse response = sharingService.sharePanel(id, principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Panel shared in view-only mode"));
    }

    @GetMapping("/api/nodes/shared-with-me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<SharedNodeResponse>>> getSharedWithMe(
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        List<SharedNodeResponse> list = sharingService.getSharedWithMe(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @DeleteMapping("/api/nodes/{id}/share/{shareId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<Void>> revokeShare(
            @AuthenticationPrincipal SecurityUserPrincipal principal,
            @PathVariable Long id,
            @PathVariable Long shareId) {
        sharingService.revokeShare(shareId, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Shared access revoked"));
    }
}
