package com.example.EduGraph.controller;

import com.example.EduGraph.dto.request.PanelRequest;
import com.example.EduGraph.dto.response.ApiResponse;
import com.example.EduGraph.dto.response.PanelResponse;
import com.example.EduGraph.security.SecurityUserPrincipal;
import com.example.EduGraph.service.PanelService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/panels")

public class PanelController {

    private final PanelService panelService;

    public PanelController(PanelService panelService) {
        this.panelService = panelService;
    }


    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<PanelResponse>> createPanel(
            @AuthenticationPrincipal SecurityUserPrincipal principal,
            @Valid @RequestBody PanelRequest request) {
        PanelResponse response = panelService.createPanel(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Subject whiteboard panel created"));
    }

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<PanelResponse>>> getMyPanels(
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        List<PanelResponse> list = panelService.getMyPanels(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<PanelResponse>> getPanel(
            @AuthenticationPrincipal SecurityUserPrincipal principal,
            @PathVariable Long id) {
        PanelResponse response = panelService.getPanel(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<PanelResponse>> updatePanel(
            @AuthenticationPrincipal SecurityUserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody PanelRequest request) {
        PanelResponse response = panelService.updatePanel(id, principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "Panel updated"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<Void>> deletePanel(
            @AuthenticationPrincipal SecurityUserPrincipal principal,
            @PathVariable Long id) {
        panelService.deletePanel(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Panel deleted successfully"));
    }
}
