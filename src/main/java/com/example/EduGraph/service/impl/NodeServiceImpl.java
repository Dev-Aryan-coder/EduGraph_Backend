package com.example.EduGraph.service.impl;

import com.example.EduGraph.dto.request.NodeRequest;
import com.example.EduGraph.dto.response.NodeResponse;
import com.example.EduGraph.entity.Node;
import com.example.EduGraph.entity.Panel;
import com.example.EduGraph.exception.ResourceNotFoundException;
import com.example.EduGraph.exception.UnauthorizedAccessException;
import com.example.EduGraph.mapper.EntityMapper;
import com.example.EduGraph.repository.NodeConnectionRepository;
import com.example.EduGraph.repository.NodeRepository;
import com.example.EduGraph.repository.PanelRepository;
import com.example.EduGraph.service.NodeService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service

public class NodeServiceImpl implements NodeService {

    private final NodeRepository nodeRepository;
    private final PanelRepository panelRepository;
    private final NodeConnectionRepository connectionRepository;
    private final EntityMapper mapper;

    public NodeServiceImpl(NodeRepository nodeRepository, PanelRepository panelRepository, NodeConnectionRepository connectionRepository, EntityMapper mapper) {
        this.nodeRepository = nodeRepository;
        this.panelRepository = panelRepository;
        this.connectionRepository = connectionRepository;
        this.mapper = mapper;
    }


    @Override
    @Transactional
    public NodeResponse createNode(Long panelId, Long studentId, NodeRequest request) {
        Panel panel = panelRepository.findById(panelId)
                .orElseThrow(() -> new ResourceNotFoundException("Panel not found with ID: " + panelId));

        if (!panel.getStudent().getId().equals(studentId)) {
            throw new UnauthorizedAccessException("You cannot add topic nodes to another student's panel.");
        }

        Node node = Node.builder()
                .panel(panel)
                .title(request.getTitle())
                .description(request.getDescription())
                .content(request.getContent())
                .positionX(request.getPositionX() != null ? request.getPositionX() : 0.0)
                .positionY(request.getPositionY() != null ? request.getPositionY() : 0.0)
                .build();

        Node saved = nodeRepository.save(node);
        return mapper.toNodeResponse(saved);
    }

    @Override
    public List<NodeResponse> getNodesForPanel(Long panelId, Long studentId) {
        Panel panel = panelRepository.findById(panelId)
                .orElseThrow(() -> new ResourceNotFoundException("Panel not found with ID: " + panelId));

        return nodeRepository.findByPanelId(panel.getId()).stream()
                .map(mapper::toNodeResponse)
                .collect(Collectors.toList());
    }

    @Override
    public NodeResponse getNode(Long nodeId, Long studentId) {
        Node node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic node not found with ID: " + nodeId));

        return mapper.toNodeResponse(node);
    }

    @Override
    @Transactional
    public NodeResponse updateNode(Long nodeId, Long studentId, NodeRequest request) {
        Node node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic node not found with ID: " + nodeId));

        if (!node.getPanel().getStudent().getId().equals(studentId)) {
            throw new UnauthorizedAccessException("You cannot modify topic nodes on another student's whiteboard.");
        }

        node.setTitle(request.getTitle());
        node.setDescription(request.getDescription());
        node.setContent(request.getContent());
        if (request.getPositionX() != null) node.setPositionX(request.getPositionX());
        if (request.getPositionY() != null) node.setPositionY(request.getPositionY());

        Node saved = nodeRepository.save(node);
        return mapper.toNodeResponse(saved);
    }

    @Override
    @Transactional
    public void deleteNode(Long nodeId, Long studentId) {
        Node node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic node not found with ID: " + nodeId));

        if (!node.getPanel().getStudent().getId().equals(studentId)) {
            throw new UnauthorizedAccessException("You cannot delete topic nodes from another student's whiteboard.");
        }

        // Cascade delete bidirectional connections
        connectionRepository.deleteConnectionsByNodeId(nodeId);
        nodeRepository.delete(node);
    }
}
