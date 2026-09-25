package com.example.EduGraph.service;

import com.example.EduGraph.dto.request.AssignmentCreateRequest;
import com.example.EduGraph.dto.request.ExtendDeadlineRequest;
import com.example.EduGraph.dto.response.AssignmentResponse;
import com.example.EduGraph.dto.response.AssignmentStatsResponse;

import java.util.List;

public interface AssignmentService {
    AssignmentResponse createAssignment(AssignmentCreateRequest request, Long teacherId);
    AssignmentResponse getAssignmentById(Long id);
    List<AssignmentResponse> getAssignmentsByClassroom(Long classroomId);
    List<AssignmentResponse> getAssignmentsForStudent(Long studentId);
    AssignmentResponse extendDeadline(Long assignmentId, ExtendDeadlineRequest request, Long teacherId);
    AssignmentStatsResponse getAssignmentStats(Long assignmentId, Long teacherId);
    List<AssignmentResponse> getAssignmentsByTeacher(Long teacherId);
    void deleteAssignment(Long assignmentId, Long teacherId);
}
