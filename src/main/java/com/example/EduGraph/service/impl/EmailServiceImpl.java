package com.example.EduGraph.service.impl;

import com.example.EduGraph.entity.User;
import com.example.EduGraph.enums.UserRole;
import com.example.EduGraph.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service

@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@edugraph.com}")
    private String fromEmail;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendCredentialsEmail(User user, String rawPassword) {
        String subject = "Welcome to EduGraph - Your Institutional Account Credentials";
        String htmlContent = """
            <div style="font-family: Arial, sans-serif; padding: 20px; color: #333; max-width: 600px; margin: auto; border: 1px solid #e0e0e0; border-radius: 8px;">
                <h2 style="color: #4f46e5; border-bottom: 2px solid #4f46e5; padding-bottom: 10px;">Welcome to EduGraph</h2>
                <p>Hello <strong>%s</strong>,</p>
                <p>Your institutional account has been successfully provisioned on EduGraph.</p>
                <div style="background-color: #f8fafc; padding: 15px; border-radius: 6px; margin: 20px 0;">
                    <p style="margin: 5px 0;"><strong>Role:</strong> %s</p>
                    <p style="margin: 5px 0;"><strong>Email:</strong> %s</p>
                    <p style="margin: 5px 0;"><strong>Temporary Password:</strong> <code style="background: #e2e8f0; padding: 2px 6px; border-radius: 4px;">%s</code></p>
                </div>
                <p>Please log in immediately and update your password.</p>
                <p style="color: #888; font-size: 12px; margin-top: 30px;">This is an automated notification from EduGraph.</p>
            </div>
            """.formatted(user.getFullName(), user.getRole().name(), user.getEmail(), rawPassword);
        sendHtmlEmail(user.getEmail(), subject, htmlContent);
    }

    @Async
    @Override
    public void sendCredentialsEmail(String toEmail, String fullName, String rawPassword, String role) {
        try {
            UserRole userRole = UserRole.valueOf(role.replace("ROLE_", "").toUpperCase());
            User dummy = User.builder().email(toEmail).fullName(fullName).role(userRole).build();
            sendCredentialsEmail(dummy, rawPassword);
        } catch (Exception e) {
            log.warn("Error parsing role {} for credentials email to {}: {}", role, toEmail, e.getMessage());
        }
    }

    
    @Async
    @Override
    public void sendOtpEmail(String toEmail, String fullName, String otp) {
        String subject = "Your EduGraph Password Reset Verification Code: " + otp;
        String htmlContent = """
            <div style="font-family: Arial, sans-serif; padding: 25px; color: #10233F; max-width: 600px; margin: auto; border: 1px solid #E2E8F0; border-radius: 12px; background-color: #FFFFFF;">
                <div style="text-align: center; margin-bottom: 24px;">
                    <h1 style="color: #10233F; font-size: 26px; margin: 0; font-weight: 800;">EduGraph</h1>
                    <p style="color: #1B7F72; font-size: 14px; margin: 4px 0 0 0; font-weight: 600;">Interactive Visual Knowledge Platform</p>
                </div>
                <div style="border-top: 1px solid #E2E8F0; padding-top: 20px;">
                    <p style="font-size: 16px;">Hello <strong>%s</strong>,</p>
                    <p style="font-size: 15px; color: #4A5568; line-height: 1.6;">
                        We received a request to reset your password for your EduGraph account. Use the 6-digit verification code below to complete the reset process:
                    </p>
                    <div style="text-align: center; margin: 30px 0;">
                        <div style="display: inline-block; background-color: #F8FAFC; border: 2px dashed #1B7F72; border-radius: 12px; padding: 18px 36px;">
                            <span style="font-family: monospace; font-size: 32px; font-weight: 800; letter-spacing: 8px; color: #10233F;">%s</span>
                        </div>
                        <p style="font-size: 13px; color: #C6822E; font-weight: 600; margin-top: 10px;">Valid for 10 minutes only</p>
                    </div>
                    <p style="font-size: 14px; color: #64748B; line-height: 1.5;">
                        If you did not request a password reset, please ignore this email or contact your institution's EduGraph coordinator.
                    </p>
                </div>
                <div style="border-top: 1px solid #E2E8F0; margin-top: 30px; padding-top: 16px; text-align: center; font-size: 12px; color: #94A3B8;">
                    &copy; 2026 EduGraph Technologies Inc. All rights reserved.
                </div>
            </div>
            """.formatted(fullName, otp);
        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    @Async
    @Override
    public void sendPasswordResetEmail(User user, String newPassword) {
        String subject = "EduGraph - Password Reset Notification";
        String htmlContent = """
            <div style="font-family: Arial, sans-serif; padding: 20px; color: #333; max-width: 600px; margin: auto; border: 1px solid #e0e0e0; border-radius: 8px;">
                <h2 style="color: #4f46e5;">EduGraph Security Alert</h2>
                <p>Hello <strong>%s</strong>,</p>
                <p>Your EduGraph account password has been reset.</p>
                <div style="background-color: #f8fafc; padding: 15px; border-radius: 6px; margin: 20px 0;">
                    <p style="margin: 5px 0;"><strong>New Password:</strong> <code style="background: #e2e8f0; padding: 2px 6px; border-radius: 4px;">%s</code></p>
                </div>
                <p>If you did not request this change, please contact your institutional administrator immediately.</p>
            </div>
            """.formatted(user.getFullName(), newPassword);
        sendHtmlEmail(user.getEmail(), subject, htmlContent);
    }

    @Async
    @Override
    public void sendAssignmentPublishedEmail(User student, String assignmentTitle, String subjectName, LocalDateTime deadline) {
        String subject = "New Assignment Published: " + assignmentTitle;
        String htmlContent = """
            <div style="font-family: Arial, sans-serif; padding: 20px; color: #333; max-width: 600px; margin: auto; border: 1px solid #e0e0e0; border-radius: 8px;">
                <h2 style="color: #2563eb;">New Assignment on EduGraph</h2>
                <p>Hello <strong>%s</strong>,</p>
                <p>A new assignment has been posted for your classroom.</p>
                <div style="background-color: #eff6ff; padding: 15px; border-radius: 6px; margin: 20px 0; border-left: 4px solid #2563eb;">
                    <p style="margin: 5px 0;"><strong>Assignment:</strong> %s</p>
                    <p style="margin: 5px 0;"><strong>Subject:</strong> %s</p>
                    <p style="margin: 5px 0;"><strong>Deadline:</strong> %s</p>
                </div>
            </div>
            """.formatted(student.getFullName(), assignmentTitle, subjectName, deadline != null ? deadline.format(FORMATTER) : "N/A");
        sendHtmlEmail(student.getEmail(), subject, htmlContent);
    }

    @Async
    @Override
    public void sendDeadlineExtensionEmail(User student, String assignmentTitle, int extraHours, String reason, LocalDateTime newDeadline) {
        String subject = "Deadline Extended: " + assignmentTitle;
        String htmlContent = """
            <div style="font-family: Arial, sans-serif; padding: 20px; color: #333; max-width: 600px; margin: auto; border: 1px solid #e0e0e0; border-radius: 8px;">
                <h2 style="color: #059669;">Assignment Deadline Extended</h2>
                <p>Hello <strong>%s</strong>,</p>
                <p>Your instructor has granted a deadline extension for <strong>%s</strong>.</p>
                <div style="background-color: #ecfdf5; padding: 15px; border-radius: 6px; margin: 20px 0; border-left: 4px solid #059669;">
                    <p style="margin: 5px 0;"><strong>Extension:</strong> +%d hours</p>
                    <p style="margin: 5px 0;"><strong>Reason:</strong> %s</p>
                    <p style="margin: 5px 0;"><strong>New Deadline:</strong> %s</p>
                </div>
            </div>
            """.formatted(student.getFullName(), assignmentTitle, extraHours, reason, newDeadline != null ? newDeadline.format(FORMATTER) : "N/A");
        sendHtmlEmail(student.getEmail(), subject, htmlContent);
    }

    @Async
    @Override
    public void sendSubmissionReceiptEmail(User student, String assignmentTitle, LocalDateTime submittedAt, double mcqScore) {
        String subject = "Submission Received: " + assignmentTitle;
        String htmlContent = """
            <div style="font-family: Arial, sans-serif; padding: 20px; color: #333; max-width: 600px; margin: auto; border: 1px solid #e0e0e0; border-radius: 8px;">
                <h2 style="color: #4f46e5;">Submission Successfully Recorded</h2>
                <p>Hello <strong>%s</strong>,</p>
                <p>Your submission for <strong>%s</strong> has been safely received.</p>
                <div style="background-color: #f5f3ff; padding: 15px; border-radius: 6px; margin: 20px 0; border-left: 4px solid #4f46e5;">
                    <p style="margin: 5px 0;"><strong>Submitted At:</strong> %s</p>
                    <p style="margin: 5px 0;"><strong>MCQ Verification Score:</strong> %.1f / 20.0</p>
                </div>
            </div>
            """.formatted(student.getFullName(), assignmentTitle, submittedAt != null ? submittedAt.format(FORMATTER) : "N/A", mcqScore);
        sendHtmlEmail(student.getEmail(), subject, htmlContent);
    }

    @Async
    @Override
    public void sendGradingResultEmail(User student, String assignmentTitle, double mcqScore, double drawingScore, double totalScore, String feedback) {
        String subject = "Grade Published: " + assignmentTitle;
        String htmlContent = """
            <div style="font-family: Arial, sans-serif; padding: 20px; color: #333; max-width: 600px; margin: auto; border: 1px solid #e0e0e0; border-radius: 8px;">
                <h2 style="color: #10b981;">Your Assignment Has Been Graded</h2>
                <p>Hello <strong>%s</strong>,</p>
                <p>Your instructor has reviewed and graded your submission for <strong>%s</strong>.</p>
                <div style="background-color: #f0fdf4; padding: 15px; border-radius: 6px; margin: 20px 0; border-left: 4px solid #10b981;">
                    <p style="margin: 5px 0;"><strong>MCQ Score:</strong> %.1f / 20.0</p>
                    <p style="margin: 5px 0;"><strong>Whiteboard Drawing Score:</strong> %.1f / 10.0</p>
                    <p style="margin: 5px 0; font-size: 16px;"><strong>Final Score:</strong> <strong>%.1f / 30.0</strong></p>
                    <p style="margin: 5px 0;"><strong>Instructor Feedback:</strong> %s</p>
                </div>
            </div>
            """.formatted(student.getFullName(), assignmentTitle, mcqScore, drawingScore, totalScore, (feedback != null && !feedback.isBlank()) ? feedback : "Good work!");
        sendHtmlEmail(student.getEmail(), subject, htmlContent);
    }

    @Async
    @Override
    public void sendTicketStatusUpdateEmail(User recipient, Long ticketId, String title, String status, String comments) {
        String subject = "Support Ticket #" + ticketId + " Status Update: " + status;
        String htmlContent = """
            <div style="font-family: Arial, sans-serif; padding: 20px; color: #333; max-width: 600px; margin: auto; border: 1px solid #e0e0e0; border-radius: 8px;">
                <h2 style="color: #0284c7;">Support Ticket Update</h2>
                <p>Hello <strong>%s</strong>,</p>
                <p>There is an update on your support ticket #%d.</p>
                <div style="background-color: #f0f9ff; padding: 15px; border-radius: 6px; margin: 20px 0; border-left: 4px solid #0284c7;">
                    <p style="margin: 5px 0;"><strong>Title:</strong> %s</p>
                    <p style="margin: 5px 0;"><strong>Current Status:</strong> %s</p>
                    <p style="margin: 5px 0;"><strong>Resolution Remarks:</strong> %s</p>
                </div>
            </div>
            """.formatted(recipient.getFullName(), ticketId, title, status, (comments != null && !comments.isBlank()) ? comments : "N/A");
        sendHtmlEmail(recipient.getEmail(), subject, htmlContent);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Email successfully dispatched to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        } catch (Exception e) {
            log.warn("Email dispatch error (SMTP service may be unconfigured in dev): {}", e.getMessage());
        }
    }
}