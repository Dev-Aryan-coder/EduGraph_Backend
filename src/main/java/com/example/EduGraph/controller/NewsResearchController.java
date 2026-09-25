package com.example.EduGraph.controller;

import com.example.EduGraph.dto.request.NewsItemRequest;
import com.example.EduGraph.dto.request.ResearchDocumentRequest;
import com.example.EduGraph.dto.response.ApiResponse;
import com.example.EduGraph.dto.response.NewsItemResponse;
import com.example.EduGraph.dto.response.ResearchDocumentResponse;
import com.example.EduGraph.security.SecurityUserPrincipal;
import com.example.EduGraph.service.NewsResearchService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feeds")

public class NewsResearchController {

    private final NewsResearchService newsResearchService;

    public NewsResearchController(NewsResearchService newsResearchService) {
        this.newsResearchService = newsResearchService;
    }

    @PostMapping("/news")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PRINCIPAL')")
    public ResponseEntity<ApiResponse<NewsItemResponse>> createNews(
            @Valid @RequestBody NewsItemRequest request,
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(newsResearchService.createNewsItem(request, principal.getId()), "News item published"));
    }

    @GetMapping("/news")
    public ResponseEntity<ApiResponse<List<NewsItemResponse>>> getAllNews() {
        return ResponseEntity.ok(ApiResponse.success(newsResearchService.getAllNewsItems()));
    }

    @PostMapping("/research")
    public ResponseEntity<ApiResponse<ResearchDocumentResponse>> uploadResearchDoc(
            @Valid @RequestBody ResearchDocumentRequest request,
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(newsResearchService.uploadResearchDocument(request, principal.getId()), "Research document uploaded"));
    }

    @GetMapping("/research")
    public ResponseEntity<ApiResponse<List<ResearchDocumentResponse>>> getAllResearchDocs() {
        return ResponseEntity.ok(ApiResponse.success(newsResearchService.getAllResearchDocuments()));
    }

    @GetMapping("/research/{id}")
    public ResponseEntity<ApiResponse<ResearchDocumentResponse>> getResearchDocById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(newsResearchService.getResearchDocumentById(id)));
    }
}
