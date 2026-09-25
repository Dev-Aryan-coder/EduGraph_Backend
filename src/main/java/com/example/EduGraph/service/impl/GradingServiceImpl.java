package com.example.EduGraph.service.impl;

import com.example.EduGraph.dto.request.GradeRequest;
import com.example.EduGraph.dto.response.SubmissionResponse;
import com.example.EduGraph.entity.Submission;
import com.example.EduGraph.entity.User;
import com.example.EduGraph.enums.SubmissionStatus;
import com.example.EduGraph.exception.BadRequestException;
import com.example.EduGraph.exception.ResourceNotFoundException;
import com.example.EduGraph.mapper.EntityMapper;
import com.example.EduGraph.repository.SubmissionRepository;
import com.example.EduGraph.repository.TabSwitchLogRepository;
import com.example.EduGraph.repository.UserRepository;
import com.example.EduGraph.service.EmailService;
import com.example.EduGraph.service.GradingService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service

@Slf4j
public class GradingServiceImpl implements GradingService {

    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final TabSwitchLogRepository tabSwitchLogRepository;
    private final EmailService emailService;
    private final EntityMapper entityMapper;

    public GradingServiceImpl(SubmissionRepository submissionRepository, UserRepository userRepository, TabSwitchLogRepository tabSwitchLogRepository, EmailService emailService, EntityMapper entityMapper) {
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
        this.tabSwitchLogRepository = tabSwitchLogRepository;
        this.emailService = emailService;
        this.entityMapper = entityMapper;
    }


    @Override
    @Transactional
    public SubmissionResponse gradeSubmission(Long submissionId, GradeRequest request, Long teacherId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        if (Boolean.TRUE.equals(request.getReject())) {
            submission.setStatus(SubmissionStatus.REJECTED);
            submission.setTeacherFeedback(request.getTeacherFeedback() != null ? request.getTeacherFeedback() : "Submission rejected by instructor. Please re-submit.");
        } else {
            if (request.getDrawingScore() == null || request.getDrawingScore() < 0 || request.getDrawingScore() > 10) {
                throw new BadRequestException("Whiteboard drawing score must be between 0.0 and 10.0");
            }
            submission.setDrawingScore(request.getDrawingScore());
            submission.setTeacherFeedback(request.getTeacherFeedback());
            submission.setStatus(SubmissionStatus.GRADED);
            submission.setGradedAt(LocalDateTime.now());
            submission.setGradedBy(teacher);

            // Compute composite score: MCQ (/20) + Drawing (/10) = 30 max
            double mcq = submission.getMcqScore() != null ? submission.getMcqScore() : 0.0;
            double drawing = request.getDrawingScore();
            double total = mcq + drawing;

            // Trigger grade notification email
            emailService.sendGradingResultEmail(
                    submission.getStudent(),
                    submission.getAssignment().getTitle(),
                    mcq,
                    drawing,
                    total,
                    request.getTeacherFeedback()
            );
        }

        Submission saved = submissionRepository.save(submission);
        return entityMapper.toSubmissionResponse(saved, tabSwitchLogRepository.findBySubmissionId(saved.getId()));
    }
}
