package com.example.EduGraph.service;

import com.example.EduGraph.dto.request.PanelRequest;
import com.example.EduGraph.dto.response.PanelResponse;

import java.util.List;

public interface PanelService {

    PanelResponse createPanel(Long studentId, PanelRequest request);

    List<PanelResponse> getMyPanels(Long studentId);

    PanelResponse getPanel(Long panelId, Long studentId);

    PanelResponse updatePanel(Long panelId, Long studentId, PanelRequest request);

    void deletePanel(Long panelId, Long studentId);
}
