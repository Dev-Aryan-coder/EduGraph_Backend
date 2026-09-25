package com.example.EduGraph.repository;

import com.example.EduGraph.entity.TabSwitchLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TabSwitchLogRepository extends JpaRepository<TabSwitchLog, Long> {

    List<TabSwitchLog> findBySubmissionId(Long submissionId);
    
    long countBySubmissionId(Long submissionId);
}