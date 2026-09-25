package com.example.EduGraph.service;

import com.example.EduGraph.dto.request.GradeRequest;
import com.example.EduGraph.dto.response.SubmissionResponse;

public interface GradingService {
    SubmissionResponse gradeSubmission(Long submissionId, GradeRequest request, Long teacherId);
}
