package com.example.EduGraph.repository;

import com.example.EduGraph.entity.Ticket;
import com.example.EduGraph.enums.TicketStatus;
import com.example.EduGraph.enums.TicketType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByTypeAndStatus(TicketType type, TicketStatus status);

    Page<Ticket> findByTypeAndStatus(TicketType type, TicketStatus status, Pageable pageable);

    List<Ticket> findByAssignedToId(Long assignedToId);

    List<Ticket> findByCreatedById(Long createdById);

    List<Ticket> findByCollegeId(Long collegeId);

    List<Ticket> findByStatus(TicketStatus status);

    List<Ticket> findByAssignedToIdAndStatus(Long assignedToId, TicketStatus status);
}