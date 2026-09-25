package com.example.EduGraph.repository;

import com.example.EduGraph.entity.ResearchDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResearchDocumentRepository extends JpaRepository<ResearchDocument, Long> {

    List<ResearchDocument> findAllByOrderByCreatedAtDesc();

    List<ResearchDocument> findByClassroomId(Long classroomId);
}