package com.example.EduGraph.service;

import com.example.EduGraph.dto.request.NewsItemRequest;
import com.example.EduGraph.dto.request.ResearchDocumentRequest;
import com.example.EduGraph.dto.response.NewsItemResponse;
import com.example.EduGraph.dto.response.ResearchDocumentResponse;

import java.util.List;

public interface NewsResearchService {
    NewsItemResponse createNewsItem(NewsItemRequest request, Long authorId);
    List<NewsItemResponse> getAllNewsItems();
    ResearchDocumentResponse uploadResearchDocument(ResearchDocumentRequest request, Long uploaderId);
    List<ResearchDocumentResponse> getAllResearchDocuments();
    ResearchDocumentResponse getResearchDocumentById(Long id);
}
