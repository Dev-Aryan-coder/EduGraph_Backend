package com.example.EduGraph.service.impl;

import com.example.EduGraph.dto.request.ResolutionRequest;
import com.example.EduGraph.dto.request.TicketRequest;
import com.example.EduGraph.dto.response.TicketResponse;
import com.example.EduGraph.entity.College;
import com.example.EduGraph.entity.Ticket;
import com.example.EduGraph.entity.User;
import com.example.EduGraph.enums.TicketStatus;
import com.example.EduGraph.enums.UserRole;
import com.example.EduGraph.exception.ResourceNotFoundException;
import com.example.EduGraph.mapper.EntityMapper;
import com.example.EduGraph.repository.CollegeRepository;
import com.example.EduGraph.repository.TicketRepository;
import com.example.EduGraph.repository.UserRepository;
import com.example.EduGraph.service.EmailService;
import com.example.EduGraph.service.TicketService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service

@Slf4j
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CollegeRepository collegeRepository;
    private final EmailService emailService;
    private final EntityMapper entityMapper;

    public TicketServiceImpl(TicketRepository ticketRepository, UserRepository userRepository, CollegeRepository collegeRepository, EmailService emailService, EntityMapper entityMapper) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.collegeRepository = collegeRepository;
        this.emailService = emailService;
        this.entityMapper = entityMapper;
    }


    @Override
    @Transactional
    public TicketResponse createTicket(TicketRequest request, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        College college = null;
        if (request.getCollegeId() != null) {
            college = collegeRepository.findById(request.getCollegeId()).orElse(null);
        } else if (creator.getCollege() != null) {
            college = creator.getCollege();
        }

        User targetUser = null;
        if (request.getTargetUserId() != null) {
            targetUser = userRepository.findById(request.getTargetUserId()).orElse(null);
        }

        // Automatic assignment to classroom coordinator or principal
        User assignedTo = null;
        if (creator.getClassroom() != null && creator.getClassroom().getCoordinator() != null) {
            assignedTo = creator.getClassroom().getCoordinator();
        }

        Ticket ticket = Ticket.builder()
                .type(request.getType())
                .status(TicketStatus.PENDING)
                .title(request.getTitle())
                .description(request.getDescription())
                .college(college)
                .createdBy(creator)
                .targetUser(targetUser)
                .requestedChanges(request.getRequestedChanges())
                .assignedTo(assignedTo)
                .build();

        Ticket saved = ticketRepository.save(ticket);

        // Async notify assigned staff if present
        if (assignedTo != null) {
            emailService.sendTicketStatusUpdateEmail(assignedTo, saved.getId(), saved.getTitle(), "NEW_ASSIGNED", saved.getDescription());
        }

        return entityMapper.toTicketResponse(saved);
    }

    @Override
    @Transactional
    public TicketResponse resolveTicket(Long ticketId, ResolutionRequest request, Long resolverId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        User resolver = userRepository.findById(resolverId)
                .orElseThrow(() -> new ResourceNotFoundException("Resolver not found"));

        ticket.setStatus(request.getStatus());
        ticket.setResolutionNotes(request.getResolutionNotes());
        ticket.setAssignedTo(resolver);
        ticket.setResolvedAt(LocalDateTime.now());

        Ticket saved = ticketRepository.save(ticket);

        // Async email creator regarding resolution
        emailService.sendTicketStatusUpdateEmail(saved.getCreatedBy(), saved.getId(), saved.getTitle(), saved.getStatus().name(), saved.getResolutionNotes());

        return entityMapper.toTicketResponse(saved);
    }

    @Override
    public List<TicketResponse> getMyTickets(Long userId) {
        return ticketRepository.findByCreatedById(userId).stream()
                .map(entityMapper::toTicketResponse)
                .toList();
    }

    @Override
    public List<TicketResponse> getPendingTicketsForStaff(Long staffId) {
        return ticketRepository.findByAssignedToIdAndStatus(staffId, TicketStatus.PENDING).stream()
                .map(entityMapper::toTicketResponse)
                .toList();
    }

    @Override
    public List<TicketResponse> getAllTicketsByCollege(Long collegeId) {
        return ticketRepository.findByCollegeId(collegeId).stream()
                .map(entityMapper::toTicketResponse)
                .toList();
    }
}
