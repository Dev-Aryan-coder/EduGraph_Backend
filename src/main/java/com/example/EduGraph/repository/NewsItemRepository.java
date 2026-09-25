package com.example.EduGraph.repository;

import com.example.EduGraph.entity.NewsItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsItemRepository extends JpaRepository<NewsItem, Long> {

    List<NewsItem> findAllByOrderByCreatedAtDesc();

    List<NewsItem> findByClassroomId(Long classroomId);
}