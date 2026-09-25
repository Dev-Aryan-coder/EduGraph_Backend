package com.example.EduGraph.repository;

import com.example.EduGraph.entity.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeRepository extends JpaRepository<Node, Long> {

    List<Node> findByPanelId(Long panelId);

    void deleteByPanelId(Long panelId);
}
