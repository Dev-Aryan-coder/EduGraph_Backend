package com.example.EduGraph.service;

import com.example.EduGraph.dto.request.SubmissionContentRequest;
import com.example.EduGraph.dto.request.TabSwitchEventRequest;
import com.example.EduGraph.dto.response.SubmissionResponse;

import java.util.List;

public interface SubmissionService {
    void recordTabSwitch(Long assignmentId, Long studentId, TabSwitchEventRequest request);
    SubmissionResponse saveDrawingDraft(Long assignmentId, Long studentId, SubmissionContentRequest request);
    SubmissionResponse finalizeSubmission(Long assignmentId, Long studentId, SubmissionContentRequest request);
    SubmissionResponse getSubmissionById(Long submissionId);
    SubmissionResponse getStudentSubmission(Long assignmentId, Long studentId);
    List<SubmissionResponse> getSubmissionsForAssignment(Long assignmentId, Long teacherId);
}
