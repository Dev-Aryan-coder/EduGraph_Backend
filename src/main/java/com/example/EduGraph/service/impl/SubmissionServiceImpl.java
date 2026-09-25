package com.example.EduGraph.service.impl;

import com.example.EduGraph.dto.request.SubmissionContentRequest;
import com.example.EduGraph.dto.request.TabSwitchEventRequest;
import com.example.EduGraph.dto.response.SubmissionResponse;
import com.example.EduGraph.entity.Assignment;
import com.example.EduGraph.entity.Submission;
import com.example.EduGraph.entity.TabSwitchLog;
import com.example.EduGraph.entity.User;
import com.example.EduGraph.enums.SubmissionStatus;
import com.example.EduGraph.enums.UserRole;
import com.example.EduGraph.exception.BadRequestException;
import com.example.EduGraph.exception.ResourceNotFoundException;
import com.example.EduGraph.exception.UnauthorizedAccessException;
import com.example.EduGraph.mapper.EntityMapper;
import com.example.EduGraph.repository.AssignmentRepository;
import com.example.EduGraph.repository.SubmissionRepository;
import com.example.EduGraph.repository.TabSwitchLogRepository;
import com.example.EduGraph.repository.UserRepository;
import com.example.EduGraph.service.EmailService;
import com.example.EduGraph.service.SubmissionService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service

@Slf4j
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final TabSwitchLogRepository tabSwitchLogRepository;
    private final EmailService emailService;
    private final EntityMapper entityMapper;

    public SubmissionServiceImpl(SubmissionRepository submissionRepository, AssignmentRepository assignmentRepository, UserRepository userRepository, TabSwitchLogRepository tabSwitchLogRepository, EmailService emailService, EntityMapper entityMapper) {
        this.submissionRepository = submissionRepository;
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
        this.tabSwitchLogRepository = tabSwitchLogRepository;
        this.emailService = emailService;
        this.entityMapper = entityMapper;
    }


    @Override
    @Transactional
    public void recordTabSwitch(Long assignmentId, Long studentId, TabSwitchEventRequest request) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Submission submission = submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId)
                .orElseGet(() -> submissionRepository.save(Submission.builder()
                        .assignment(assignment)
                        .student(student)
                        .status(SubmissionStatus.PENDING)
                        .tabSwitchCount(0)
                        .build()));

        int count = submission.getTabSwitchCount() != null ? submission.getTabSwitchCount() + 1 : 1;
        submission.setTabSwitchCount(count);
        submissionRepository.save(submission);

        TabSwitchLog logEntry = TabSwitchLog.builder()
                .submission(submission)
                .switchedAt(request.getTimestamp() != null ? request.getTimestamp() : LocalDateTime.now())
                .reason(request.getDetails() != null ? request.getDetails() : "Window blur detected during active assessment")
                .build();
        tabSwitchLogRepository.save(logEntry);

        log.warn("Security Alert: Student {} [ID: {}] tab-switch event recorded. Total: {}",
                student.getFullName(), studentId, count);
    }

    @Override
    @Transactional
    public SubmissionResponse saveDrawingDraft(Long assignmentId, Long studentId, SubmissionContentRequest request) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Submission submission = submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId)
                .orElseGet(() -> Submission.builder()
                        .assignment(assignment)
                        .student(student)
                        .status(SubmissionStatus.PENDING)
                        .tabSwitchCount(0)
                        .build());

        submission.setExcalidrawDrawingData(request.getExcalidrawDrawingData());
        Submission saved = submissionRepository.save(submission);

        List<TabSwitchLog> logs = tabSwitchLogRepository.findBySubmissionId(saved.getId());
        return entityMapper.toSubmissionResponse(saved, logs);
    }

    @Override
    @Transactional
    public SubmissionResponse finalizeSubmission(Long assignmentId, Long studentId, SubmissionContentRequest request) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (assignment.getDeadline().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Assignment deadline has passed. Late submissions require an approved extension.");
        }

        Submission submission = submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId)
                .orElseThrow(() -> new BadRequestException("You must complete the 20-question MCQ quiz before final submission"));

        if (submission.getMcqScore() == null) {
            throw new BadRequestException("MCQ assessment must be completed and scored before final whiteboard submission");
        }

        if (request.getExcalidrawDrawingData() != null) {
            submission.setExcalidrawDrawingData(request.getExcalidrawDrawingData());
        }

        submission.setStatus(SubmissionStatus.SUBMITTED);
        submission.setSubmittedAt(LocalDateTime.now());
        Submission saved = submissionRepository.save(submission);

        emailService.sendSubmissionReceiptEmail(student, assignment.getTitle(), saved.getSubmittedAt(), saved.getMcqScore());

        List<TabSwitchLog> logs = tabSwitchLogRepository.findBySubmissionId(saved.getId());
        return entityMapper.toSubmissionResponse(saved, logs);
    }

    @Override
    public SubmissionResponse getSubmissionById(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));
        List<TabSwitchLog> logs = tabSwitchLogRepository.findBySubmissionId(submissionId);
        return entityMapper.toSubmissionResponse(submission, logs);
    }

    @Override
    public SubmissionResponse getStudentSubmission(Long assignmentId, Long studentId) {
        Submission submission = submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("No submission record found"));
        List<TabSwitchLog> logs = tabSwitchLogRepository.findBySubmissionId(submission.getId());
        return entityMapper.toSubmissionResponse(submission, logs);
    }

    @Override
    public List<SubmissionResponse> getSubmissionsForAssignment(Long assignmentId, Long teacherId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        if (!assignment.getTeacher().getId().equals(teacherId)) {
            User teacher = userRepository.findById(teacherId).orElseThrow();
            if (teacher.getRole() != UserRole.ADMIN && teacher.getRole() != UserRole.PRINCIPAL) {
                throw new UnauthorizedAccessException("Access denied to submission roster");
            }
        }
        List<Submission> submissions = submissionRepository.findByAssignmentId(assignmentId);
        return submissions.stream()
                .map(s -> entityMapper.toSubmissionResponse(s, tabSwitchLogRepository.findBySubmissionId(s.getId())))
                .toList();
    }
}