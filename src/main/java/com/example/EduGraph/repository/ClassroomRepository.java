package com.example.EduGraph.repository;

import com.example.EduGraph.entity.Classroom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {

    List<Classroom> findByCollegeId(Long collegeId);

    Page<Classroom> findByCollegeId(Long collegeId, Pageable pageable);

    List<Classroom> findByTeacherId(Long teacherId);

    long countByCollegeId(Long collegeId);
}
