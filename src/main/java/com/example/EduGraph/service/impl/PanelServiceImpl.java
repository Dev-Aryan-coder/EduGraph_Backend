package com.example.EduGraph.service.impl;

import com.example.EduGraph.dto.request.PanelRequest;
import com.example.EduGraph.dto.response.PanelResponse;
import com.example.EduGraph.entity.Node;
import com.example.EduGraph.entity.Panel;
import com.example.EduGraph.entity.User;
import com.example.EduGraph.exception.ResourceNotFoundException;
import com.example.EduGraph.exception.UnauthorizedAccessException;
import com.example.EduGraph.mapper.EntityMapper;
import com.example.EduGraph.repository.NodeConnectionRepository;
import com.example.EduGraph.repository.NodeRepository;
import com.example.EduGraph.repository.PanelRepository;
import com.example.EduGraph.repository.UserRepository;
import com.example.EduGraph.service.PanelService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service

public class PanelServiceImpl implements PanelService {

    private final PanelRepository panelRepository;
    private final NodeRepository nodeRepository;
    private final NodeConnectionRepository connectionRepository;
    private final UserRepository userRepository;
    private final EntityMapper mapper;

    public PanelServiceImpl(PanelRepository panelRepository, NodeRepository nodeRepository, NodeConnectionRepository connectionRepository, UserRepository userRepository, EntityMapper mapper) {
        this.panelRepository = panelRepository;
        this.nodeRepository = nodeRepository;
        this.connectionRepository = connectionRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }


    @Override
    @Transactional
    public PanelResponse createPanel(Long studentId, PanelRequest request) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        Panel panel = Panel.builder()
                .subjectName(request.getSubjectName())
                .student(student)
                .build();

        Panel saved = panelRepository.save(panel);
        return mapper.toPanelResponse(saved, 0);
    }

    @Override
    public List<PanelResponse> getMyPanels(Long studentId) {
        return panelRepository.findByStudentId(studentId).stream()
                .map(p -> {
                    int count = nodeRepository.findByPanelId(p.getId()).size();
                    return mapper.toPanelResponse(p, count);
                })
                .collect(Collectors.toList());
    }

    @Override
    public PanelResponse getPanel(Long panelId, Long studentId) {
        Panel panel = panelRepository.findById(panelId)
                .orElseThrow(() -> new ResourceNotFoundException("Panel not found with ID: " + panelId));

        if (!panel.getStudent().getId().equals(studentId)) {
            throw new UnauthorizedAccessException("You do not have access to this whiteboard panel.");
        }

        int count = nodeRepository.findByPanelId(panel.getId()).size();
        return mapper.toPanelResponse(panel, count);
    }

    @Override
    @Transactional
    public PanelResponse updatePanel(Long panelId, Long studentId, PanelRequest request) {
        Panel panel = panelRepository.findById(panelId)
                .orElseThrow(() -> new ResourceNotFoundException("Panel not found with ID: " + panelId));

        if (!panel.getStudent().getId().equals(studentId)) {
            throw new UnauthorizedAccessException("You do not have permission to modify this whiteboard panel.");
        }

        panel.setSubjectName(request.getSubjectName());
        Panel saved = panelRepository.save(panel);
        int count = nodeRepository.findByPanelId(panel.getId()).size();
        return mapper.toPanelResponse(saved, count);
    }

    @Override
    @Transactional
    public void deletePanel(Long panelId, Long studentId) {
        Panel panel = panelRepository.findById(panelId)
                .orElseThrow(() -> new ResourceNotFoundException("Panel not found with ID: " + panelId));

        if (!panel.getStudent().getId().equals(studentId)) {
            throw new UnauthorizedAccessException("You do not have permission to delete this whiteboard panel.");
        }

        // Cascade delete all connections referencing nodes in this panel
        List<Node> nodes = nodeRepository.findByPanelId(panelId);
        for (Node node : nodes) {
            connectionRepository.deleteConnectionsByNodeId(node.getId());
        }

        nodeRepository.deleteByPanelId(panelId);
        panelRepository.delete(panel);
    }
}
