package com.example.EduGraph.controller;

import com.example.EduGraph.dto.request.NodeRequest;
import com.example.EduGraph.dto.response.ApiResponse;
import com.example.EduGraph.dto.response.NodeResponse;
import com.example.EduGraph.security.SecurityUserPrincipal;
import com.example.EduGraph.service.NodeService;
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

public class NodeController {

    private final NodeService nodeService;

    public NodeController(NodeService nodeService) {
        this.nodeService = nodeService;
    }


    @PostMapping("/api/panels/{panelId}/nodes")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<NodeResponse>> createNode(
            @AuthenticationPrincipal SecurityUserPrincipal principal,
            @PathVariable Long panelId,
            @Valid @RequestBody NodeRequest request) {
        NodeResponse response = nodeService.createNode(panelId, principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Topic node created"));
    }

    @GetMapping("/api/panels/{panelId}/nodes")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<NodeResponse>>> getNodesForPanel(
            @AuthenticationPrincipal SecurityUserPrincipal principal,
            @PathVariable Long panelId) {
        List<NodeResponse> list = nodeService.getNodesForPanel(panelId, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/api/nodes/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<NodeResponse>> getNode(
            @AuthenticationPrincipal SecurityUserPrincipal principal,
            @PathVariable Long id) {
        NodeResponse response = nodeService.getNode(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/api/nodes/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<NodeResponse>> updateNode(
            @AuthenticationPrincipal SecurityUserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody NodeRequest request) {
        NodeResponse response = nodeService.updateNode(id, principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "Topic node updated"));
    }

    @DeleteMapping("/api/nodes/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<Void>> deleteNode(
            @AuthenticationPrincipal SecurityUserPrincipal principal,
            @PathVariable Long id) {
        nodeService.deleteNode(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Topic node deleted"));
    }
}
