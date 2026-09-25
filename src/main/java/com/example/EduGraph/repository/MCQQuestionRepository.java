package com.example.EduGraph.repository;

import com.example.EduGraph.entity.MCQQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MCQQuestionRepository extends JpaRepository<MCQQuestion, Long> {

    List<MCQQuestion> findByAssignmentId(Long assignmentId);

    List<MCQQuestion> findByAssignmentIdOrderByQuestionNumberAsc(Long assignmentId);

    int countByAssignmentId(Long assignmentId);
}