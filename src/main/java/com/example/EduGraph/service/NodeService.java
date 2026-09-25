package com.example.EduGraph.service;

import com.example.EduGraph.dto.request.NodeRequest;
import com.example.EduGraph.dto.response.NodeResponse;

import java.util.List;

public interface NodeService {

    NodeResponse createNode(Long panelId, Long studentId, NodeRequest request);

    List<NodeResponse> getNodesForPanel(Long panelId, Long studentId);

    NodeResponse getNode(Long nodeId, Long studentId);

    NodeResponse updateNode(Long nodeId, Long studentId, NodeRequest request);

    void deleteNode(Long nodeId, Long studentId);
}
