package com.example.EduGraph.service.impl;

import com.example.EduGraph.dto.request.NodeConnectionRequest;
import com.example.EduGraph.dto.response.NodeConnectionResponse;
import com.example.EduGraph.entity.Node;
import com.example.EduGraph.entity.NodeConnection;
import com.example.EduGraph.exception.BadRequestException;
import com.example.EduGraph.exception.ResourceNotFoundException;
import com.example.EduGraph.exception.UnauthorizedAccessException;
import com.example.EduGraph.mapper.EntityMapper;
import com.example.EduGraph.repository.NodeConnectionRepository;
import com.example.EduGraph.repository.NodeRepository;
import com.example.EduGraph.service.NodeConnectionService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service

public class NodeConnectionServiceImpl implements NodeConnectionService {

    private final NodeConnectionRepository connectionRepository;
    private final NodeRepository nodeRepository;
    private final EntityMapper mapper;

    public NodeConnectionServiceImpl(NodeConnectionRepository connectionRepository, NodeRepository nodeRepository, EntityMapper mapper) {
        this.connectionRepository = connectionRepository;
        this.nodeRepository = nodeRepository;
        this.mapper = mapper;
    }


    @Override
    @Transactional
    public NodeConnectionResponse linkNodes(Long sourceNodeId, Long studentId, NodeConnectionRequest request) {
        if (sourceNodeId.equals(request.getTargetNodeId())) {
            throw new BadRequestException("A topic node cannot be linked to itself.");
        }

        Node sourceNode = nodeRepository.findById(sourceNodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Source node not found with ID: " + sourceNodeId));

        Node targetNode = nodeRepository.findById(request.getTargetNodeId())
                .orElseThrow(() -> new ResourceNotFoundException("Target node not found with ID: " + request.getTargetNodeId()));

        if (!sourceNode.getPanel().getStudent().getId().equals(studentId)) {
            throw new UnauthorizedAccessException("You can only link nodes on your own whiteboard panels.");
        }

        NodeConnection connection = NodeConnection.builder()
                .sourceNode(sourceNode)
                .targetNode(targetNode)
                .label(request.getLabel())
                .build();

        NodeConnection saved = connectionRepository.save(connection);
        return mapper.toNodeConnectionResponse(saved);
    }

    @Override
    public List<NodeConnectionResponse> getConnections(Long nodeId, Long studentId) {
        // Query bidirectional connections so node drawer shows both incoming and outgoing links
        return connectionRepository.findConnectionsByNodeId(nodeId).stream()
                .map(mapper::toNodeConnectionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void unlinkNodes(Long connectionId, Long studentId) {
        NodeConnection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection not found with ID: " + connectionId));

        if (!connection.getSourceNode().getPanel().getStudent().getId().equals(studentId)) {
            throw new UnauthorizedAccessException("You do not have permission to remove this connection.");
        }

        connectionRepository.delete(connection);
    }
}
