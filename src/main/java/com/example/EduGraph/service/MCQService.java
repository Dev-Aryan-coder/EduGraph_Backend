package com.example.EduGraph.service;

import com.example.EduGraph.dto.request.MCQAnswerRequest;
import com.example.EduGraph.dto.response.MCQQuestionResponse;
import com.example.EduGraph.dto.response.MCQResultResponse;

import java.util.List;

public interface MCQService {
    List<MCQQuestionResponse> getQuestionsForStudent(Long assignmentId, Long studentId);
    List<MCQQuestionResponse> getQuestionsForTeacher(Long assignmentId, Long teacherId);
    MCQResultResponse submitMCQAnswers(Long assignmentId, Long studentId, List<MCQAnswerRequest> answers);
    MCQResultResponse getStudentMCQResult(Long assignmentId, Long studentId);
}
