package com.example.EduGraph.service;

import com.example.EduGraph.dto.request.ResolutionRequest;
import com.example.EduGraph.dto.request.TicketRequest;
import com.example.EduGraph.dto.response.TicketResponse;

import java.util.List;

public interface TicketService {
    TicketResponse createTicket(TicketRequest request, Long creatorId);
    TicketResponse resolveTicket(Long ticketId, ResolutionRequest request, Long resolverId);
    List<TicketResponse> getMyTickets(Long userId);
    List<TicketResponse> getPendingTicketsForStaff(Long staffId);
    List<TicketResponse> getAllTicketsByCollege(Long collegeId);
}
