package com.example.EduGraph.service.impl;

import com.example.EduGraph.dto.request.CoordinatorCreateRequest;
import com.example.EduGraph.dto.request.PrincipalRegisterRequest;
import com.example.EduGraph.dto.response.CollegeOverviewResponse;
import com.example.EduGraph.dto.response.LoginResponse;
import com.example.EduGraph.dto.response.UserResponse;
import com.example.EduGraph.entity.College;
import com.example.EduGraph.entity.User;
import com.example.EduGraph.enums.AccountStatus;
import com.example.EduGraph.enums.UserRole;
import com.example.EduGraph.exception.BadRequestException;
import com.example.EduGraph.exception.ResourceNotFoundException;
import com.example.EduGraph.mapper.EntityMapper;
import com.example.EduGraph.repository.ClassroomRepository;
import com.example.EduGraph.repository.CollegeRepository;
import com.example.EduGraph.repository.UserRepository;
import com.example.EduGraph.security.JwtUtil;
import com.example.EduGraph.service.CollegeService;
import com.example.EduGraph.service.EmailService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service

public class CollegeServiceImpl implements CollegeService {

    private final CollegeRepository collegeRepository;
    private final UserRepository userRepository;
    private final ClassroomRepository classroomRepository;
    private final JwtUtil jwtUtil;
    private final EntityMapper mapper;
    private final EmailService emailService;

    public CollegeServiceImpl(CollegeRepository collegeRepository, UserRepository userRepository, ClassroomRepository classroomRepository, JwtUtil jwtUtil, EntityMapper mapper, EmailService emailService) {
        this.collegeRepository = collegeRepository;
        this.userRepository = userRepository;
        this.classroomRepository = classroomRepository;
        this.jwtUtil = jwtUtil;
        this.mapper = mapper;
        this.emailService = emailService;
    }


    @Override
    @Transactional
    public LoginResponse registerPrincipalAndCollege(PrincipalRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("An account with email " + request.getEmail() + " already exists.");
        }

        College college = College.builder()
                .name(request.getCollegeName())
                .address(request.getCollegeAddress())
                .contactEmail(request.getContactEmail())
                .build();
        College savedCollege = collegeRepository.save(college);

        User principal = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(UserRole.PRINCIPAL)
                .status(AccountStatus.ACTIVE)
                .college(savedCollege)
                .build();
        User savedPrincipal = userRepository.save(principal);

        String token = jwtUtil.generateToken(savedPrincipal.getId(), savedPrincipal.getEmail(), UserRole.PRINCIPAL.name(), savedCollege.getId(), null);

        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(savedPrincipal.getId())
                .email(savedPrincipal.getEmail())
                .fullName(savedPrincipal.getFullName())
                .role(UserRole.PRINCIPAL.name())
                .collegeId(savedCollege.getId())
                .collegeName(savedCollege.getName())
                .build();
    }

    @Override
    @Transactional
    public UserResponse createCoordinator(Long principalCollegeId, CoordinatorCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("An account with email " + request.getEmail() + " already exists.");
        }

        College college = collegeRepository.findById(principalCollegeId)
                .orElseThrow(() -> new ResourceNotFoundException("College not found with ID: " + principalCollegeId));

        User coordinator = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(UserRole.COORDINATOR)
                .status(AccountStatus.ACTIVE)
                .college(college)
                .phoneNumber(request.getPhoneNumber())
                .build();

        User saved = userRepository.save(coordinator);

        emailService.sendCredentialsEmail(saved.getEmail(), saved.getFullName(), request.getPassword(), UserRole.COORDINATOR.name());

        return mapper.toUserResponse(saved);
    }

    @Override
    public List<UserResponse> getCoordinators(Long collegeId) {
        return userRepository.findByCollegeIdAndRole(collegeId, UserRole.COORDINATOR).stream()
                .map(mapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CollegeOverviewResponse getCollegeOverview(Long collegeId) {
        College college = collegeRepository.findById(collegeId)
                .orElseThrow(() -> new ResourceNotFoundException("College not found with ID: " + collegeId));

        long coordinators = userRepository.countByCollegeIdAndRole(collegeId, UserRole.COORDINATOR);
        long teachers = userRepository.countByCollegeIdAndRole(collegeId, UserRole.TEACHER);
        long students = userRepository.countByCollegeIdAndRole(collegeId, UserRole.STUDENT);
        long classrooms = classroomRepository.countByCollegeId(collegeId);

        return CollegeOverviewResponse.builder()
                .collegeId(college.getId())
                .collegeName(college.getName())
                .totalCoordinators(coordinators)
                .totalTeachers(teachers)
                .totalStudents(students)
                .totalClassrooms(classrooms)
                .build();
    }
}
