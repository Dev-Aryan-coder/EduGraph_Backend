package com.example.EduGraph.repository;

import com.example.EduGraph.entity.NodeConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeConnectionRepository extends JpaRepository<NodeConnection, Long> {

    @Query("SELECT nc FROM NodeConnection nc WHERE nc.sourceNode.id = :nodeId OR nc.targetNode.id = :nodeId")
    List<NodeConnection> findConnectionsByNodeId(@Param("nodeId") Long nodeId);

    @Modifying
    @Query("DELETE FROM NodeConnection nc WHERE nc.sourceNode.id = :nodeId OR nc.targetNode.id = :nodeId")
    void deleteConnectionsByNodeId(@Param("nodeId") Long nodeId);
}
