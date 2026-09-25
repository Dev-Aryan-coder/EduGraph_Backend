package com.example.EduGraph.service.impl;

import com.example.EduGraph.dto.request.ShareRequest;
import com.example.EduGraph.dto.response.SharedNodeResponse;
import com.example.EduGraph.entity.Node;
import com.example.EduGraph.entity.Panel;
import com.example.EduGraph.entity.SharedNode;
import com.example.EduGraph.entity.User;
import com.example.EduGraph.exception.BadRequestException;
import com.example.EduGraph.exception.ResourceNotFoundException;
import com.example.EduGraph.exception.UnauthorizedAccessException;
import com.example.EduGraph.mapper.EntityMapper;
import com.example.EduGraph.repository.NodeRepository;
import com.example.EduGraph.repository.PanelRepository;
import com.example.EduGraph.repository.SharedNodeRepository;
import com.example.EduGraph.repository.UserRepository;
import com.example.EduGraph.service.NodeSharingService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service

public class NodeSharingServiceImpl implements NodeSharingService {

    private final SharedNodeRepository sharedNodeRepository;
    private final NodeRepository nodeRepository;
    private final PanelRepository panelRepository;
    private final UserRepository userRepository;
    private final EntityMapper mapper;

    public NodeSharingServiceImpl(SharedNodeRepository sharedNodeRepository, NodeRepository nodeRepository, PanelRepository panelRepository, UserRepository userRepository, EntityMapper mapper) {
        this.sharedNodeRepository = sharedNodeRepository;
        this.nodeRepository = nodeRepository;
        this.panelRepository = panelRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }


    @Override
    @Transactional
    public SharedNodeResponse shareNode(Long nodeId, Long studentId, ShareRequest request) {
        Node node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Node not found with ID: " + nodeId));

        if (!node.getPanel().getStudent().getId().equals(studentId)) {
            throw new UnauthorizedAccessException("You can only share your own topic nodes.");
        }

        User recipient = userRepository.findByEmail(request.getRecipientEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Recipient student not found with email: " + request.getRecipientEmail()));

        if (recipient.getId().equals(studentId)) {
            throw new BadRequestException("You cannot share a node with yourself.");
        }

        SharedNode sharedNode = SharedNode.builder()
                .node(node)
                .sharedBy(node.getPanel().getStudent())
                .sharedWith(recipient)
                .permission("VIEW_ONLY")
                .build();

        SharedNode saved = sharedNodeRepository.save(sharedNode);
        return mapper.toSharedNodeResponse(saved);
    }

    @Override
    @Transactional
    public SharedNodeResponse sharePanel(Long panelId, Long studentId, ShareRequest request) {
        Panel panel = panelRepository.findById(panelId)
                .orElseThrow(() -> new ResourceNotFoundException("Panel not found with ID: " + panelId));

        if (!panel.getStudent().getId().equals(studentId)) {
            throw new UnauthorizedAccessException("You can only share your own whiteboard panels.");
        }

        User recipient = userRepository.findByEmail(request.getRecipientEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Recipient student not found with email: " + request.getRecipientEmail()));

        if (recipient.getId().equals(studentId)) {
            throw new BadRequestException("You cannot share a panel with yourself.");
        }

        SharedNode sharedNode = SharedNode.builder()
                .panel(panel)
                .sharedBy(panel.getStudent())
                .sharedWith(recipient)
                .permission("VIEW_ONLY")
                .build();

        SharedNode saved = sharedNodeRepository.save(sharedNode);
        return mapper.toSharedNodeResponse(saved);
    }

    @Override
    public List<SharedNodeResponse> getSharedWithMe(Long studentId) {
        return sharedNodeRepository.findBySharedWithId(studentId).stream()
                .map(mapper::toSharedNodeResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void revokeShare(Long shareId, Long studentId) {
        SharedNode share = sharedNodeRepository.findById(shareId)
                .orElseThrow(() -> new ResourceNotFoundException("Shared record not found with ID: " + shareId));

        if (!share.getSharedBy().getId().equals(studentId)) {
            throw new UnauthorizedAccessException("You do not have permission to revoke this sharing grant.");
        }

        sharedNodeRepository.delete(share);
    }
}
