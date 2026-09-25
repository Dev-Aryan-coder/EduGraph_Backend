package com.example.EduGraph.controller;

import com.example.EduGraph.dto.response.*;
import com.example.EduGraph.entity.User;
import com.example.EduGraph.enums.AccountStatus;
import com.example.EduGraph.enums.TicketStatus;
import com.example.EduGraph.exception.ResourceNotFoundException;
import com.example.EduGraph.mapper.EntityMapper;
import com.example.EduGraph.repository.CollegeRepository;
import com.example.EduGraph.repository.TicketRepository;
import com.example.EduGraph.repository.UserRepository;
import com.example.EduGraph.security.SecurityUserPrincipal;
import com.example.EduGraph.service.AdminDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminPlatformController {

    private final AdminDashboardService adminDashboardService;
    private final CollegeRepository collegeRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final EntityMapper mapper;

    public AdminPlatformController(AdminDashboardService adminDashboardService,
                                  CollegeRepository collegeRepository,
                                  UserRepository userRepository,
                                  TicketRepository ticketRepository,
                                  EntityMapper mapper) {
        this.adminDashboardService = adminDashboardService;
        this.collegeRepository = collegeRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.mapper = mapper;
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PRINCIPAL')")
    public ResponseEntity<ApiResponse<AdminDashboardStatsResponse>> getPlatformStats(
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        AdminDashboardStatsResponse stats = adminDashboardService.getDashboardStats(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/colleges")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<CollegeResponse>>> getAllColleges() {
        List<CollegeResponse> list = collegeRepository.findAll().stream()
                .map(mapper::toCollegeResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> list = userRepository.findAll().stream()
                .map(mapper::toUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/tickets")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getAllTickets() {
        List<TicketResponse> list = ticketRepository.findAll().stream()
                .map(mapper::toTicketResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PutMapping("/users/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> toggleUserStatus(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        user.setStatus(user.getStatus() == AccountStatus.ACTIVE ? AccountStatus.SUSPENDED : AccountStatus.ACTIVE);
        User saved = userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success(mapper.toUserResponse(saved), "User status updated to " + saved.getStatus()));
    }
}