package com.example.EduGraph.service;

import com.example.EduGraph.dto.request.ShareRequest;
import com.example.EduGraph.dto.response.SharedNodeResponse;

import java.util.List;

public interface NodeSharingService {

    SharedNodeResponse shareNode(Long nodeId, Long studentId, ShareRequest request);

    SharedNodeResponse sharePanel(Long panelId, Long studentId, ShareRequest request);

    List<SharedNodeResponse> getSharedWithMe(Long studentId);

    void revokeShare(Long shareId, Long studentId);
}
