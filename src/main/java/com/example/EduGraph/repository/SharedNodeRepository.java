package com.example.EduGraph.repository;

import com.example.EduGraph.entity.SharedNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SharedNodeRepository extends JpaRepository<SharedNode, Long> {

    List<SharedNode> findBySharedWithId(Long sharedWithId);

    List<SharedNode> findByNodeId(Long nodeId);

    List<SharedNode> findByPanelId(Long panelId);
}
