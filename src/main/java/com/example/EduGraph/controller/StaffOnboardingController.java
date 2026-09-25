package com.example.EduGraph.controller;

import com.example.EduGraph.dto.request.ExcelImportConfirmRequest;
import com.example.EduGraph.dto.response.ApiResponse;
import com.example.EduGraph.dto.response.ClassroomResponse;
import com.example.EduGraph.dto.response.ExcelPreviewResponse;
import com.example.EduGraph.dto.response.PageResponse;
import com.example.EduGraph.dto.response.UserResponse;
import com.example.EduGraph.entity.Classroom;
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
import com.example.EduGraph.security.SecurityUserPrincipal;
import com.example.EduGraph.service.EmailService;
import com.example.EduGraph.service.ExcelImportService;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/coordinator")
public class StaffOnboardingController {

    private final ExcelImportService excelImportService;
    private final UserRepository userRepository;
    private final ClassroomRepository classroomRepository;
    private final CollegeRepository collegeRepository;
    private final EntityMapper mapper;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public StaffOnboardingController(ExcelImportService excelImportService,
                                     UserRepository userRepository,
                                     ClassroomRepository classroomRepository,
                                     CollegeRepository collegeRepository,
                                     EntityMapper mapper,
                                     EmailService emailService,
                                     PasswordEncoder passwordEncoder) {
        this.excelImportService = excelImportService;
        this.userRepository = userRepository;
        this.classroomRepository = classroomRepository;
        this.collegeRepository = collegeRepository;
        this.mapper = mapper;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/overview")
    @PreAuthorize("hasRole('COORDINATOR')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOverview(
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        long students = userRepository.countByCollegeIdAndRole(principal.getCollegeId(), UserRole.STUDENT);
        long teachers = userRepository.countByCollegeIdAndRole(principal.getCollegeId(), UserRole.TEACHER);
        long classrooms = classroomRepository.countByCollegeId(principal.getCollegeId());
        College college = collegeRepository.findById(principal.getCollegeId()).orElse(null);

        Map<String, Object> data = new HashMap<>();
        data.put("totalStudents", students);
        data.put("totalTeachers", teachers);
        data.put("totalClassrooms", classrooms);
        data.put("collegeId", principal.getCollegeId());
        data.put("collegeName", college != null ? college.getName() : "Institution");
        data.put("coordinatorName", principal.getFullName());

        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/classrooms")
    @PreAuthorize("hasRole('COORDINATOR')")
    public ResponseEntity<ApiResponse<List<ClassroomResponse>>> getClassrooms(
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        List<Classroom> list = classroomRepository.findByCollegeId(principal.getCollegeId());
        List<ClassroomResponse> responses = list.stream().map(c -> {
            int count = userRepository.findByClassroomIdAndRole(c.getId(), UserRole.STUDENT).size();
            return mapper.toClassroomResponse(c, count);
        }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PostMapping("/classrooms")
    @PreAuthorize("hasRole('COORDINATOR')")
    public ResponseEntity<ApiResponse<ClassroomResponse>> createClassroom(
            @AuthenticationPrincipal SecurityUserPrincipal principal,
            @RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        String section = (String) body.get("section");
        String academicYear = (body.get("academicYear") != null && !body.get("academicYear").toString().trim().isEmpty())
                ? body.get("academicYear").toString().trim()
                : "2026-2027";
        Long teacherId = body.get("teacherId") != null && !body.get("teacherId").toString().isEmpty()
                ? Long.valueOf(body.get("teacherId").toString())
                : null;

        College college = collegeRepository.findById(principal.getCollegeId())
                .orElseThrow(() -> new ResourceNotFoundException("College not found"));
        User coordinator = userRepository.findById(principal.getId()).orElse(null);
        User teacher = teacherId != null ? userRepository.findById(teacherId).orElse(null) : null;

        Classroom classroom = Classroom.builder()
                .name(name)
                .section(section)
                .academicYear(academicYear)
                .college(college)
                .coordinator(coordinator)
                .teacher(teacher)
                .build();

        Classroom saved = classroomRepository.save(classroom);
        return ResponseEntity.ok(ApiResponse.success(mapper.toClassroomResponse(saved, 0), "Classroom created successfully"));
    }

    @DeleteMapping("/classrooms/{id}")
    @PreAuthorize("hasRole('COORDINATOR')")
    public ResponseEntity<ApiResponse<Void>> deleteClassroom(
            @PathVariable Long id,
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with ID: " + id));
        if (!classroom.getCollege().getId().equals(principal.getCollegeId())) {
            throw new BadRequestException("Unauthorized access to this classroom");
        }
        classroomRepository.delete(classroom);
        return ResponseEntity.ok(ApiResponse.success(null, "Classroom removed"));
    }

    @PostMapping("/students")
    @PreAuthorize("hasRole('COORDINATOR')")
    public ResponseEntity<ApiResponse<UserResponse>> createStudent(
            @AuthenticationPrincipal SecurityUserPrincipal principal,
            @RequestBody Map<String, Object> body) {
        String email = ((String) body.get("email")).trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("User with email " + email + " already exists");
        }
        College college = collegeRepository.findById(principal.getCollegeId())
                .orElseThrow(() -> new ResourceNotFoundException("College not found"));
        Long classroomId = body.get("classroomId") != null && !body.get("classroomId").toString().isEmpty()
                ? Long.valueOf(body.get("classroomId").toString())
                : null;
        Classroom classroom = classroomId != null ? classroomRepository.findById(classroomId).orElse(null) : null;

        SecureRandom random = new SecureRandom();
        String rawPassword = "Student@" + (100000 + random.nextInt(900000));

        User student = User.builder()
                .fullName((String) body.get("fullName"))
                .email(email)
                .rollNumber((String) body.get("rollNumber"))
                .phoneNumber((String) body.get("phoneNumber"))
                .password(passwordEncoder.encode(rawPassword))
                .role(UserRole.STUDENT)
                .status(AccountStatus.ACTIVE)
                .college(college)
                .classroom(classroom)
                .build();

        User saved = userRepository.save(student);
        emailService.sendCredentialsEmail(saved.getEmail(), saved.getFullName(), rawPassword, "STUDENT");

        return ResponseEntity.ok(ApiResponse.success(mapper.toUserResponse(saved), "Student enrolled and credentials dispatched via email"));
    }

    @PostMapping("/teachers")
    @PreAuthorize("hasRole('COORDINATOR')")
    public ResponseEntity<ApiResponse<UserResponse>> createTeacher(
            @AuthenticationPrincipal SecurityUserPrincipal principal,
            @RequestBody Map<String, Object> body) {
        String email = ((String) body.get("email")).trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("User with email " + email + " already exists");
        }
        College college = collegeRepository.findById(principal.getCollegeId())
                .orElseThrow(() -> new ResourceNotFoundException("College not found"));

        SecureRandom random = new SecureRandom();
        String rawPassword = "Teacher@" + (100000 + random.nextInt(900000));

        User teacher = User.builder()
                .fullName((String) body.get("fullName"))
                .email(email)
                .phoneNumber((String) body.get("phoneNumber"))
                .password(passwordEncoder.encode(rawPassword))
                .role(UserRole.TEACHER)
                .status(AccountStatus.ACTIVE)
                .college(college)
                .build();

        User saved = userRepository.save(teacher);
        emailService.sendCredentialsEmail(saved.getEmail(), saved.getFullName(), rawPassword, "TEACHER");

        return ResponseEntity.ok(ApiResponse.success(mapper.toUserResponse(saved), "Teacher onboarded and credentials dispatched via email"));
    }

    @PostMapping("/import/students/preview")
    @PreAuthorize("hasRole('COORDINATOR')")
    public ResponseEntity<ApiResponse<ExcelPreviewResponse>> previewStudents(@RequestParam("file") MultipartFile file) {
        ExcelPreviewResponse preview = excelImportService.previewStudentImport(file);
        return ResponseEntity.ok(ApiResponse.success(preview, "Student sheet parsed in-memory"));
    }

    @PutMapping("/import/students/confirm")
    @PreAuthorize("hasRole('COORDINATOR')")
    public ResponseEntity<ApiResponse<Integer>> confirmStudents(
            @AuthenticationPrincipal SecurityUserPrincipal principal,
            @Valid @RequestBody ExcelImportConfirmRequest request) {
        int count = excelImportService.confirmStudentImport(principal.getCollegeId(), request);
        return ResponseEntity.ok(ApiResponse.success(count, "Successfully registered " + count + " students and dispatched credentials"));
    }

    @PostMapping("/import/teachers/preview")
    @PreAuthorize("hasRole('COORDINATOR')")
    public ResponseEntity<ApiResponse<ExcelPreviewResponse>> previewTeachers(@RequestParam("file") MultipartFile file) {
        ExcelPreviewResponse preview = excelImportService.previewTeacherImport(file);
        return ResponseEntity.ok(ApiResponse.success(preview, "Teacher sheet parsed in-memory"));
    }

    @PutMapping("/import/teachers/confirm")
    @PreAuthorize("hasRole('COORDINATOR')")
    public ResponseEntity<ApiResponse<Integer>> confirmTeachers(
            @AuthenticationPrincipal SecurityUserPrincipal principal,
            @Valid @RequestBody ExcelImportConfirmRequest request) {
        int count = excelImportService.confirmTeacherImport(principal.getCollegeId(), request);
        return ResponseEntity.ok(ApiResponse.success(count, "Successfully registered " + count + " teachers and dispatched credentials"));
    }

    @GetMapping("/students")
    @PreAuthorize("hasRole('COORDINATOR')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getStudents(
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        List<User> list = userRepository.findByCollegeIdAndRole(principal.getCollegeId(), UserRole.STUDENT);
        return ResponseEntity.ok(ApiResponse.success(list.stream().map(mapper::toUserResponse).collect(Collectors.toList())));
    }

    @GetMapping("/teachers")
    @PreAuthorize("hasRole('COORDINATOR')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getTeachers(
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        List<User> list = userRepository.findByCollegeIdAndRole(principal.getCollegeId(), UserRole.TEACHER);
        return ResponseEntity.ok(ApiResponse.success(list.stream().map(mapper::toUserResponse).collect(Collectors.toList())));
    }

    @PostMapping("/users/{id}/resend-credentials")
    @PreAuthorize("hasRole('COORDINATOR')")
    public ResponseEntity<ApiResponse<Void>> resendCredentials(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        SecureRandom random = new SecureRandom();
        String rolePrefix = user.getRole() == UserRole.TEACHER ? "Teacher@" : "Student@";
        String newPassword = rolePrefix + (100000 + random.nextInt(900000));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        emailService.sendCredentialsEmail(user.getEmail(), user.getFullName(), newPassword, user.getRole().name());
        return ResponseEntity.ok(ApiResponse.success(null, "New credentials dispatched to " + user.getEmail()));
    }
}