package com.example.EduGraph.repository;

import com.example.EduGraph.entity.Panel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PanelRepository extends JpaRepository<Panel, Long> {

    List<Panel> findByStudentId(Long studentId);
}
