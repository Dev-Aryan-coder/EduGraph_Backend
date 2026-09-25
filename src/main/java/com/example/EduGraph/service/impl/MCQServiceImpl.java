package com.example.EduGraph.service.impl;

import com.example.EduGraph.dto.request.MCQAnswerRequest;
import com.example.EduGraph.dto.response.MCQQuestionResponse;
import com.example.EduGraph.dto.response.MCQResultResponse;
import com.example.EduGraph.entity.Assignment;
import com.example.EduGraph.entity.MCQAttempt;
import com.example.EduGraph.entity.MCQQuestion;
import com.example.EduGraph.entity.Submission;
import com.example.EduGraph.entity.User;
import com.example.EduGraph.enums.SubmissionStatus;
import com.example.EduGraph.exception.BadRequestException;
import com.example.EduGraph.exception.ResourceNotFoundException;
import com.example.EduGraph.mapper.EntityMapper;
import com.example.EduGraph.repository.AssignmentRepository;
import com.example.EduGraph.repository.MCQAttemptRepository;
import com.example.EduGraph.repository.MCQQuestionRepository;
import com.example.EduGraph.repository.SubmissionRepository;
import com.example.EduGraph.repository.UserRepository;
import com.example.EduGraph.service.MCQService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service

@Slf4j
public class MCQServiceImpl implements MCQService {

    private final MCQQuestionRepository mcqQuestionRepository;
    private final MCQAttemptRepository mcqAttemptRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;

    public MCQServiceImpl(MCQQuestionRepository mcqQuestionRepository, MCQAttemptRepository mcqAttemptRepository, AssignmentRepository assignmentRepository, SubmissionRepository submissionRepository, UserRepository userRepository, EntityMapper entityMapper) {
        this.mcqQuestionRepository = mcqQuestionRepository;
        this.mcqAttemptRepository = mcqAttemptRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
        this.entityMapper = entityMapper;
    }


    @Override
    public List<MCQQuestionResponse> getQuestionsForStudent(Long assignmentId, Long studentId) {
        List<MCQQuestion> questions = mcqQuestionRepository.findByAssignmentIdOrderByQuestionNumberAsc(assignmentId);
        return questions.stream()
                .map(q -> MCQQuestionResponse.builder()
                        .id(q.getId())
                        .assignmentId(assignmentId)
                        .questionNumber(q.getQuestionNumber())
                        .questionText(q.getQuestionText())
                        .optionA(q.getOptionA())
                        .optionB(q.getOptionB())
                        .optionC(q.getOptionC())
                        .optionD(q.getOptionD())
                        .correctOption(null)
                        .explanation(null)
                        .build())
                .toList();
    }

    @Override
    public List<MCQQuestionResponse> getQuestionsForTeacher(Long assignmentId, Long teacherId) {
        List<MCQQuestion> questions = mcqQuestionRepository.findByAssignmentIdOrderByQuestionNumberAsc(assignmentId);
        return questions.stream()
                .map(entityMapper::toMCQQuestionResponse)
                .toList();
    }

    @Override
    @Transactional
    public MCQResultResponse submitMCQAnswers(Long assignmentId, Long studentId, List<MCQAnswerRequest> answers) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        List<MCQQuestion> questions = mcqQuestionRepository.findByAssignmentIdOrderByQuestionNumberAsc(assignmentId);
        if (questions.isEmpty()) {
            throw new BadRequestException("No MCQ questions configured for this assignment");
        }

        Map<Long, MCQQuestion> questionMap = questions.stream()
                .collect(Collectors.toMap(MCQQuestion::getId, q -> q));

        Submission submission = submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId)
                .orElseGet(() -> Submission.builder()
                        .assignment(assignment)
                        .student(student)
                        .status(SubmissionStatus.PENDING)
                        .tabSwitchCount(0)
                        .build());

        int correctCount = 0;
        List<MCQResultResponse.MCQBreakdownItem> breakdown = new ArrayList<>();
        List<MCQAttempt> attempts = new ArrayList<>();

        for (MCQAnswerRequest answer : answers) {
            MCQQuestion q = questionMap.get(answer.getQuestionId());
            if (q == null) continue;

            boolean isCorrect = q.getCorrectOption().equalsIgnoreCase(answer.getSelectedOption().trim());
            if (isCorrect) correctCount++;

            MCQAttempt attempt = MCQAttempt.builder()
                    .submission(submission)
                    .question(q)
                    .selectedOption(answer.getSelectedOption().toUpperCase())
                    .isCorrect(isCorrect)
                    .build();
            attempts.add(attempt);

            breakdown.add(MCQResultResponse.MCQBreakdownItem.builder()
                    .questionId(q.getId())
                    .questionNumber(q.getQuestionNumber())
                    .questionText(q.getQuestionText())
                    .selectedOption(answer.getSelectedOption().toUpperCase())
                    .correctOption(q.getCorrectOption())
                    .isCorrect(isCorrect)
                    .explanation(q.getExplanation())
                    .build());
        }

        double score = (double) correctCount;
        double percentage = (score / questions.size()) * 100.0;
        boolean passed = percentage >= 50.0;

        submission.setMcqScore(score);
        if (submission.getStatus() == SubmissionStatus.PENDING) {
            submission.setStatus(SubmissionStatus.MCQ_COMPLETED);
        }
        submissionRepository.save(submission);

        mcqAttemptRepository.deleteBySubmissionId(submission.getId());
        mcqAttemptRepository.saveAll(attempts);

        return MCQResultResponse.builder()
                .totalQuestions(questions.size())
                .correctAnswers(correctCount)
                .score(score)
                .percentage(Math.round(percentage * 100.0) / 100.0)
                .passed(passed)
                .breakdown(breakdown)
                .build();
    }

    @Override
    public MCQResultResponse getStudentMCQResult(Long assignmentId, Long studentId) {
        Submission submission = submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("No submission record found for student"));

        List<MCQAttempt> attempts = mcqAttemptRepository.findBySubmissionId(submission.getId());
        int total = (int) mcqQuestionRepository.countByAssignmentId(assignmentId);
        int correct = (int) attempts.stream().filter(MCQAttempt::isCorrect).count();

        List<MCQResultResponse.MCQBreakdownItem> breakdown = attempts.stream()
                .map(a -> MCQResultResponse.MCQBreakdownItem.builder()
                        .questionId(a.getQuestion().getId())
                        .questionNumber(a.getQuestion().getQuestionNumber())
                        .questionText(a.getQuestion().getQuestionText())
                        .selectedOption(a.getSelectedOption())
                        .correctOption(a.getQuestion().getCorrectOption())
                        .isCorrect(a.isCorrect())
                        .explanation(a.getQuestion().getExplanation())
                        .build())
                .toList();

        double score = submission.getMcqScore() != null ? submission.getMcqScore() : correct;
        double pct = total > 0 ? (score / total) * 100.0 : 0.0;

        return MCQResultResponse.builder()
                .totalQuestions(total)
                .correctAnswers(correct)
                .score(score)
                .percentage(Math.round(pct * 100.0) / 100.0)
                .passed(pct >= 50.0)
                .breakdown(breakdown)
                .build();
    }
}