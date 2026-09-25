package com.example.EduGraph.service;

import com.example.EduGraph.entity.User;

import java.time.LocalDateTime;

public interface EmailService {
    void sendCredentialsEmail(User user, String rawPassword);
    void sendCredentialsEmail(String toEmail, String fullName, String rawPassword, String role);
    void sendPasswordResetEmail(User user, String newPassword);
    void sendAssignmentPublishedEmail(User student, String assignmentTitle, String subject, LocalDateTime deadline);
    void sendDeadlineExtensionEmail(User student, String assignmentTitle, int extraHours, String reason, LocalDateTime newDeadline);
    void sendSubmissionReceiptEmail(User student, String assignmentTitle, LocalDateTime submittedAt, double mcqScore);
    void sendGradingResultEmail(User student, String assignmentTitle, double mcqScore, double drawingScore, double totalScore, String feedback);
    void sendTicketStatusUpdateEmail(User recipient, Long ticketId, String title, String status, String comments);
}