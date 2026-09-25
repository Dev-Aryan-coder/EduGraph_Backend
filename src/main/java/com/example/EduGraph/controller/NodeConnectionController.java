package com.example.EduGraph.controller;

import com.example.EduGraph.dto.request.NodeConnectionRequest;
import com.example.EduGraph.dto.response.ApiResponse;
import com.example.EduGraph.dto.response.NodeConnectionResponse;
import com.example.EduGraph.security.SecurityUserPrincipal;
import com.example.EduGraph.service.NodeConnectionService;
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
@RequestMapping("/api/nodes/{id}/connections")

public class NodeConnectionController {

    private final NodeConnectionService connectionService;

    public NodeConnectionController(NodeConnectionService connectionService) {
        this.connectionService = connectionService;
    }


    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<NodeConnectionResponse>> linkNodes(
            @AuthenticationPrincipal SecurityUserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody NodeConnectionRequest request) {
        NodeConnectionResponse response = connectionService.linkNodes(id, principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Topic nodes linked successfully"));
    }

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<NodeConnectionResponse>>> getConnections(
            @AuthenticationPrincipal SecurityUserPrincipal principal,
            @PathVariable Long id) {
        List<NodeConnectionResponse> list = connectionService.getConnections(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @DeleteMapping("/{connId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<Void>> unlinkNodes(
            @AuthenticationPrincipal SecurityUserPrincipal principal,
            @PathVariable Long id,
            @PathVariable Long connId) {
        connectionService.unlinkNodes(connId, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Connection unlinked successfully"));
    }
}
