package com.example.EduGraph.repository;

import com.example.EduGraph.entity.MCQAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MCQAttemptRepository extends JpaRepository<MCQAttempt, Long> {

    List<MCQAttempt> findBySubmissionId(Long submissionId);

    void deleteBySubmissionId(Long submissionId);
}