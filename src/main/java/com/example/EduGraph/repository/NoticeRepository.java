package com.example.EduGraph.repository;

import com.example.EduGraph.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findByCollegeId(Long collegeId);

    List<Notice> findByCollegeIdOrClassroomId(Long collegeId, Long classroomId);

    List<Notice> findByClassroomId(Long classroomId);
}