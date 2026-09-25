package com.example.EduGraph.service.impl;

import com.example.EduGraph.dto.request.NewsItemRequest;
import com.example.EduGraph.dto.request.ResearchDocumentRequest;
import com.example.EduGraph.dto.response.NewsItemResponse;
import com.example.EduGraph.dto.response.ResearchDocumentResponse;
import com.example.EduGraph.entity.NewsItem;
import com.example.EduGraph.entity.ResearchDocument;
import com.example.EduGraph.entity.User;
import com.example.EduGraph.exception.ResourceNotFoundException;
import com.example.EduGraph.mapper.EntityMapper;
import com.example.EduGraph.repository.NewsItemRepository;
import com.example.EduGraph.repository.ResearchDocumentRepository;
import com.example.EduGraph.repository.UserRepository;
import com.example.EduGraph.service.NewsResearchService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service

@Slf4j
public class NewsResearchServiceImpl implements NewsResearchService {

    private final NewsItemRepository newsItemRepository;
    private final ResearchDocumentRepository researchDocumentRepository;
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;

    public NewsResearchServiceImpl(NewsItemRepository newsItemRepository, ResearchDocumentRepository researchDocumentRepository, UserRepository userRepository, EntityMapper entityMapper) {
        this.newsItemRepository = newsItemRepository;
        this.researchDocumentRepository = researchDocumentRepository;
        this.userRepository = userRepository;
        this.entityMapper = entityMapper;
    }


    @Override
    @Transactional
    public NewsItemResponse createNewsItem(NewsItemRequest request, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));

        NewsItem news = NewsItem.builder()
                .title(request.getTitle())
                .summary(request.getSummary())
                .content(request.getContent())
                .sourceUrl(request.getSourceUrl())
                .imageUrl(request.getImageUrl())
                .category(request.getCategory() != null ? request.getCategory() : "EdTech")
                .author(author)
                .build();

        NewsItem saved = newsItemRepository.save(news);
        return entityMapper.toNewsItemResponse(saved);
    }

    @Override
    public List<NewsItemResponse> getAllNewsItems() {
        return newsItemRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(entityMapper::toNewsItemResponse)
                .toList();
    }

    @Override
    @Transactional
    public ResearchDocumentResponse uploadResearchDocument(ResearchDocumentRequest request, Long uploaderId) {
        User uploader = userRepository.findById(uploaderId)
                .orElseThrow(() -> new ResourceNotFoundException("Uploader not found"));

        ResearchDocument doc = ResearchDocument.builder()
                .title(request.getTitle())
                .abstractText(request.getAbstractText())
                .authors(request.getAuthors())
                .journalOrConference(request.getJournalOrConference())
                .publicationDate(request.getPublicationDate())
                .documentFileUrl(request.getDocumentFileUrl())
                .uploadedBy(uploader)
                .build();

        ResearchDocument saved = researchDocumentRepository.save(doc);
        return entityMapper.toResearchDocumentResponse(saved);
    }

    @Override
    public List<ResearchDocumentResponse> getAllResearchDocuments() {
        return researchDocumentRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(entityMapper::toResearchDocumentResponse)
                .toList();
    }

    @Override
    public ResearchDocumentResponse getResearchDocumentById(Long id) {
        ResearchDocument doc = researchDocumentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Research document not found with id: " + id));
        return entityMapper.toResearchDocumentResponse(doc);
    }
}
