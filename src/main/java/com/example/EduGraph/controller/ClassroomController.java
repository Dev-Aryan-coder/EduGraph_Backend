package com.example.EduGraph.controller;

import com.example.EduGraph.dto.response.ApiResponse;
import com.example.EduGraph.dto.response.ClassroomResponse;
import com.example.EduGraph.dto.response.UserResponse;
import com.example.EduGraph.entity.Classroom;
import com.example.EduGraph.entity.User;
import com.example.EduGraph.enums.UserRole;
import com.example.EduGraph.mapper.EntityMapper;
import com.example.EduGraph.repository.ClassroomRepository;
import com.example.EduGraph.repository.UserRepository;
import com.example.EduGraph.security.SecurityUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/classrooms")
public class ClassroomController {

    private final ClassroomRepository classroomRepository;
    private final UserRepository userRepository;
    private final EntityMapper mapper;

    public ClassroomController(ClassroomRepository classroomRepository,
                               UserRepository userRepository,
                               EntityMapper mapper) {
        this.classroomRepository = classroomRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @GetMapping("/my-classrooms")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ClassroomResponse>>> getMyClassrooms(
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        List<Classroom> list;
        User user = userRepository.findById(principal.getId()).orElse(null);
        if (user != null && user.getRole() == UserRole.TEACHER) {
            list = classroomRepository.findByTeacherId(principal.getId());
        } else if (user != null && (user.getRole() == UserRole.COORDINATOR || user.getRole() == UserRole.PRINCIPAL)) {
            list = classroomRepository.findByCollegeId(principal.getCollegeId());
        } else if (user != null && user.getClassroom() != null) {
            list = List.of(user.getClassroom());
        } else {
            list = List.of();
        }

        List<ClassroomResponse> responses = list.stream().map(c -> {
            int count = userRepository.findByClassroomIdAndRole(c.getId(), UserRole.STUDENT).size();
            return mapper.toClassroomResponse(c, count);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ClassroomResponse>> getClassroomById(@PathVariable Long id) {
        Classroom c = classroomRepository.findById(id).orElse(null);
        if (c == null) {
            return ResponseEntity.notFound().build();
        }
        int count = userRepository.findByClassroomIdAndRole(c.getId(), UserRole.STUDENT).size();
        return ResponseEntity.ok(ApiResponse.success(mapper.toClassroomResponse(c, count)));
    }

    @GetMapping("/{id}/students")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_COORDINATOR', 'ROLE_PRINCIPAL', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getClassroomStudents(@PathVariable Long id) {
        List<User> students = userRepository.findByClassroomIdAndRole(id, UserRole.STUDENT);
        List<UserResponse> responses = students.stream().map(mapper::toUserResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}