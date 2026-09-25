package com.example.EduGraph.service.impl;

import com.example.EduGraph.dto.request.AssignmentCreateRequest;
import com.example.EduGraph.dto.request.ExtendDeadlineRequest;
import com.example.EduGraph.dto.request.MCQQuestionRequest;
import com.example.EduGraph.dto.response.AssignmentResponse;
import com.example.EduGraph.dto.response.AssignmentStatsResponse;
import com.example.EduGraph.entity.Assignment;
import com.example.EduGraph.entity.Classroom;
import com.example.EduGraph.entity.MCQQuestion;
import com.example.EduGraph.entity.Submission;
import com.example.EduGraph.entity.User;
import com.example.EduGraph.enums.SubmissionStatus;
import com.example.EduGraph.enums.UserRole;
import com.example.EduGraph.exception.BadRequestException;
import com.example.EduGraph.exception.ResourceNotFoundException;
import com.example.EduGraph.exception.UnauthorizedAccessException;
import com.example.EduGraph.mapper.EntityMapper;
import com.example.EduGraph.repository.AssignmentRepository;
import com.example.EduGraph.repository.ClassroomRepository;
import com.example.EduGraph.repository.MCQQuestionRepository;
import com.example.EduGraph.repository.SubmissionRepository;
import com.example.EduGraph.repository.UserRepository;
import com.example.EduGraph.service.AssignmentService;
import com.example.EduGraph.service.EmailService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service

@Slf4j
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final ClassroomRepository classroomRepository;
    private final UserRepository userRepository;
    private final MCQQuestionRepository mcqQuestionRepository;
    private final SubmissionRepository submissionRepository;
    private final EmailService emailService;
    private final EntityMapper entityMapper;

    public AssignmentServiceImpl(AssignmentRepository assignmentRepository, ClassroomRepository classroomRepository, UserRepository userRepository, MCQQuestionRepository mcqQuestionRepository, SubmissionRepository submissionRepository, EmailService emailService, EntityMapper entityMapper) {
        this.assignmentRepository = assignmentRepository;
        this.classroomRepository = classroomRepository;
        this.userRepository = userRepository;
        this.mcqQuestionRepository = mcqQuestionRepository;
        this.submissionRepository = submissionRepository;
        this.emailService = emailService;
        this.entityMapper = entityMapper;
    }


    @Override
    @Transactional
    public AssignmentResponse createAssignment(AssignmentCreateRequest request, Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        Classroom classroom = classroomRepository.findById(request.getClassroomId())
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found"));

        if (request.getQuestions() == null || request.getQuestions().size() != 20) {
            throw new BadRequestException("An assignment must have exactly 20 MCQ questions for comprehensive verification");
        }

        if (request.getDeadline().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Deadline cannot be in the past");
        }

        Assignment assignment = Assignment.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .classroom(classroom)
                .teacher(teacher)
                .subject(request.getSubject())
                .deadline(request.getDeadline())
                .excalidrawTemplateData(request.getExcalidrawTemplateData())
                .extensionGrantedHours(0)
                .build();

        Assignment savedAssignment = assignmentRepository.save(assignment);

        List<MCQQuestion> questions = new ArrayList<>();
        int qNum = 1;
        for (MCQQuestionRequest qr : request.getQuestions()) {
            MCQQuestion q = MCQQuestion.builder()
                    .assignment(savedAssignment)
                    .questionNumber(qr.getQuestionNumber() != null ? qr.getQuestionNumber() : qNum++)
                    .questionText(qr.getQuestionText())
                    .optionA(qr.getOptionA())
                    .optionB(qr.getOptionB())
                    .optionC(qr.getOptionC())
                    .optionD(qr.getOptionD())
                    .correctOption(qr.getCorrectOption().toUpperCase())
                    .explanation(qr.getExplanation())
                    .build();
            questions.add(q);
        }
        mcqQuestionRepository.saveAll(questions);

        // Async notify students in the classroom
        List<User> students = userRepository.findByClassroomId(classroom.getId());
        for (User student : students) {
            emailService.sendAssignmentPublishedEmail(student, savedAssignment.getTitle(), savedAssignment.getSubject(), savedAssignment.getDeadline());
        }

        return entityMapper.toAssignmentResponse(savedAssignment, questions.size());
    }

    @Override
    public AssignmentResponse getAssignmentById(Long id) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + id));
        int mcqCount = (int) mcqQuestionRepository.countByAssignmentId(id);
        return entityMapper.toAssignmentResponse(assignment, mcqCount);
    }

    @Override
    public List<AssignmentResponse> getAssignmentsByClassroom(Long classroomId) {
        List<Assignment> list = assignmentRepository.findByClassroomId(classroomId);
        return list.stream()
                .map(a -> entityMapper.toAssignmentResponse(a, (int) mcqQuestionRepository.countByAssignmentId(a.getId())))
                .toList();
    }

        @Override
    public List<AssignmentResponse> getAssignmentsByTeacher(Long teacherId) {
        List<Assignment> list = assignmentRepository.findByTeacherId(teacherId);
        return list.stream()
                .map(a -> entityMapper.toAssignmentResponse(a, (int) mcqQuestionRepository.countByAssignmentId(a.getId())))
                .toList();
    }

    @Override
    public List<AssignmentResponse> getAssignmentsForStudent(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        if (student.getClassroom() == null) {
            return List.of();
        }
        return getAssignmentsByClassroom(student.getClassroom().getId());
    }

    @Override
    @Transactional
    public AssignmentResponse extendDeadline(Long assignmentId, ExtendDeadlineRequest request, Long teacherId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        if (!assignment.getTeacher().getId().equals(teacherId)) {
            User current = userRepository.findById(teacherId).orElseThrow();
            if (current.getRole() != UserRole.ADMIN && current.getRole() != UserRole.PRINCIPAL) {
                throw new UnauthorizedAccessException("Only the assigning instructor or institution heads can extend deadlines");
            }
        }

        if (request.getExtensionHours() > 48) {
            throw new BadRequestException("Maximum permitted deadline extension is 48 hours (2 days)");
        }

        int currentExtension = assignment.getExtensionGrantedHours() != null ? assignment.getExtensionGrantedHours() : 0;
        if (currentExtension + request.getExtensionHours() > 48) {
            throw new BadRequestException("Cumulative extension cannot exceed 48 hours. Already granted: " + currentExtension + " hours.");
        }

        assignment.setExtensionGrantedHours(currentExtension + request.getExtensionHours());
        assignment.setExtensionReason(request.getReason());
        assignment.setDeadline(assignment.getDeadline().plusHours(request.getExtensionHours()));

        Assignment updated = assignmentRepository.save(assignment);

        // Async notify students
        List<User> students = userRepository.findByClassroomId(updated.getClassroom().getId());
        for (User student : students) {
            emailService.sendDeadlineExtensionEmail(student, updated.getTitle(), request.getExtensionHours(), request.getReason(), updated.getDeadline());
        }

        return entityMapper.toAssignmentResponse(updated, (int) mcqQuestionRepository.countByAssignmentId(updated.getId()));
    }

    @Override
    public AssignmentStatsResponse getAssignmentStats(Long assignmentId, Long teacherId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        List<User> students = userRepository.findByClassroomId(assignment.getClassroom().getId());
        List<Submission> submissions = submissionRepository.findByAssignmentId(assignmentId);

        int totalStudents = students.size();
        int submittedCount = (int) submissions.stream().filter(s -> s.getStatus() == SubmissionStatus.SUBMITTED || s.getStatus() == SubmissionStatus.GRADED).count();
        int gradedCount = (int) submissions.stream().filter(s -> s.getStatus() == SubmissionStatus.GRADED).count();
        int pendingCount = totalStudents - submittedCount;

        double avgScore = submissions.stream()
                .filter(s -> s.getStatus() == SubmissionStatus.GRADED && s.getMcqScore() != null && s.getDrawingScore() != null)
                .mapToDouble(s -> s.getMcqScore() + s.getDrawingScore())
                .average()
                .orElse(0.0);

        int flaggedTabSwitches = (int) submissions.stream()
                .filter(s -> s.getTabSwitchCount() != null && s.getTabSwitchCount() > 3)
                .count();

        return AssignmentStatsResponse.builder()
                .assignmentId(assignmentId)
                .assignmentTitle(assignment.getTitle())
                .totalStudents(totalStudents)
                .submittedCount(submittedCount)
                .gradedCount(gradedCount)
                .pendingCount(pendingCount)
                .averageScore(Math.round(avgScore * 100.0) / 100.0)
                .flaggedTabSwitchCount(flaggedTabSwitches)
                .build();
    }

    @Override
    @Transactional
    public void deleteAssignment(Long assignmentId, Long teacherId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        if (!assignment.getTeacher().getId().equals(teacherId)) {
            throw new UnauthorizedAccessException("You can only delete assignments created by yourself");
        }
        assignmentRepository.delete(assignment);
    }
}