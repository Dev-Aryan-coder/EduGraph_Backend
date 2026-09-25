package com.example.EduGraph.service;

import com.example.EduGraph.dto.request.NodeConnectionRequest;
import com.example.EduGraph.dto.response.NodeConnectionResponse;

import java.util.List;

public interface NodeConnectionService {

    NodeConnectionResponse linkNodes(Long sourceNodeId, Long studentId, NodeConnectionRequest request);

    List<NodeConnectionResponse> getConnections(Long nodeId, Long studentId);

    void unlinkNodes(Long connectionId, Long studentId);
}
