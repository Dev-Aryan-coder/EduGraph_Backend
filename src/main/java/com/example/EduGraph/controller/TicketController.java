package com.example.EduGraph.controller;

import com.example.EduGraph.dto.request.ResolutionRequest;
import com.example.EduGraph.dto.request.TicketRequest;
import com.example.EduGraph.dto.response.ApiResponse;
import com.example.EduGraph.dto.response.TicketResponse;
import com.example.EduGraph.security.SecurityUserPrincipal;
import com.example.EduGraph.service.TicketService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")

public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TicketResponse>> createTicket(
            @Valid @RequestBody TicketRequest request,
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        TicketResponse response = ticketService.createTicket(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Support ticket submitted successfully"));
    }

    @PostMapping("/{id}/resolve")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_COORDINATOR')")
    public ResponseEntity<ApiResponse<TicketResponse>> resolveTicket(
            @PathVariable Long id,
            @Valid @RequestBody ResolutionRequest request,
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        TicketResponse response = ticketService.resolveTicket(id, request, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Ticket status updated and email sent to user"));
    }

    @GetMapping("/my-tickets")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getMyTickets(
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(ticketService.getMyTickets(principal.getId())));
    }

    @GetMapping("/pending-review")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_COORDINATOR')")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getPendingTickets(
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(ticketService.getPendingTicketsForStaff(principal.getId())));
    }

    @GetMapping("/college/{collegeId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PRINCIPAL')")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getAllTicketsForCollege(@PathVariable Long collegeId) {
        return ResponseEntity.ok(ApiResponse.success(ticketService.getAllTicketsByCollege(collegeId)));
    }
}