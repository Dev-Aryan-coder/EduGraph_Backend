package com.example.EduGraph.service.impl;

import com.example.EduGraph.dto.response.AdminDashboardStatsResponse;
import com.example.EduGraph.enums.TicketStatus;
import com.example.EduGraph.enums.UserRole;
import com.example.EduGraph.repository.*;
import com.example.EduGraph.service.AdminDashboardService;

import org.springframework.stereotype.Service;

@Service

public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final CollegeRepository collegeRepository;
    private final ClassroomRepository classroomRepository;
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final TicketRepository ticketRepository;
    private final NoticeRepository noticeRepository;

    public AdminDashboardServiceImpl(CollegeRepository collegeRepository, ClassroomRepository classroomRepository, UserRepository userRepository, AssignmentRepository assignmentRepository, SubmissionRepository submissionRepository, TicketRepository ticketRepository, NoticeRepository noticeRepository) {
        this.collegeRepository = collegeRepository;
        this.classroomRepository = classroomRepository;
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.ticketRepository = ticketRepository;
        this.noticeRepository = noticeRepository;
    }


    @Override
    public AdminDashboardStatsResponse getDashboardStats(Long adminId) {
        return AdminDashboardStatsResponse.builder()
                .totalColleges(collegeRepository.count())
                .totalClassrooms(classroomRepository.count())
                .totalUsers(userRepository.count())
                .totalStudents(userRepository.countByRole(UserRole.STUDENT))
                .totalTeachers(userRepository.countByRole(UserRole.TEACHER))
                .totalCoordinators(userRepository.countByRole(UserRole.COORDINATOR))
                .totalAssignments(assignmentRepository.count())
                .totalSubmissions(submissionRepository.count())
                .pendingTicketsCount((long) ticketRepository.findByStatus(TicketStatus.PENDING).size())
                .activeNoticesCount(noticeRepository.count())
                .build();
    }
}