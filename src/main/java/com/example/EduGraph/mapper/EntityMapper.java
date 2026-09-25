package com.example.EduGraph.mapper;

import com.example.EduGraph.dto.response.*;
import com.example.EduGraph.entity.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class EntityMapper {

    public CollegeResponse toCollegeResponse(College college) {
        if (college == null) return null;
        return CollegeResponse.builder()
                .id(college.getId())
                .name(college.getName())
                .address(college.getAddress())
                .contactEmail(college.getContactEmail())
                .build();
    }

    public CollegeResponse toCollegeResponse(College college, int classroomCount, int userCount) {
        return toCollegeResponse(college);
    }

    public ClassroomResponse toClassroomResponse(Classroom classroom, int studentCount) {
        if (classroom == null) return null;
        return ClassroomResponse.builder()
                .id(classroom.getId())
                .name(classroom.getName())
                .section(classroom.getSection())
                .academicYear(classroom.getAcademicYear())
                .collegeId(classroom.getCollege() != null ? classroom.getCollege().getId() : null)
                .collegeName(classroom.getCollege() != null ? classroom.getCollege().getName() : null)
                .teacherId(classroom.getTeacher() != null ? classroom.getTeacher().getId() : null)
                .teacherName(classroom.getTeacher() != null ? classroom.getTeacher().getFullName() : null)
                .coordinatorId(classroom.getCoordinator() != null ? classroom.getCoordinator().getId() : null)
                .coordinatorName(classroom.getCoordinator() != null ? classroom.getCoordinator().getFullName() : null)
                .studentCount(studentCount)
                .createdAt(classroom.getCreatedAt())
                .build();
    }

    public UserResponse toUserResponse(User user) {
        if (user == null) return null;
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .collegeId(user.getCollege() != null ? user.getCollege().getId() : null)
                .collegeName(user.getCollege() != null ? user.getCollege().getName() : null)
                .classroomId(user.getClassroom() != null ? user.getClassroom().getId() : null)
                .classroomName(user.getClassroom() != null ? user.getClassroom().getName() : null)
                .rollNumber(user.getRollNumber())
                .phoneNumber(user.getPhoneNumber())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }

    public PanelResponse toPanelResponse(Panel panel, int nodeCount) {
        if (panel == null) return null;
        return PanelResponse.builder()
                .id(panel.getId())
                .subjectName(panel.getSubjectName())
                .studentId(panel.getStudent() != null ? panel.getStudent().getId() : null)
                .studentName(panel.getStudent() != null ? panel.getStudent().getFullName() : null)
                .nodeCount(nodeCount)
                .createdAt(panel.getCreatedAt())
                .updatedAt(panel.getUpdatedAt())
                .build();
    }

    public NodeResponse toNodeResponse(Node node) {
        if (node == null) return null;
        return NodeResponse.builder()
                .id(node.getId())
                .panelId(node.getPanel() != null ? node.getPanel().getId() : null)
                .subjectName(node.getPanel() != null ? node.getPanel().getSubjectName() : null)
                .title(node.getTitle())
                .description(node.getDescription())
                .content(node.getContent())
                .positionX(node.getPositionX())
                .positionY(node.getPositionY())
                .createdAt(node.getCreatedAt())
                .updatedAt(node.getUpdatedAt())
                .build();
    }

    public NodeResponse toNodeResponse(Node node, int connectionCount) {
        return toNodeResponse(node);
    }

    public NodeConnectionResponse toNodeConnectionResponse(NodeConnection conn) {
        if (conn == null) return null;
        return NodeConnectionResponse.builder()
                .id(conn.getId())
                .sourceNodeId(conn.getSourceNode() != null ? conn.getSourceNode().getId() : null)
                .sourceNodeTitle(conn.getSourceNode() != null ? conn.getSourceNode().getTitle() : null)
                .targetNodeId(conn.getTargetNode() != null ? conn.getTargetNode().getId() : null)
                .targetNodeTitle(conn.getTargetNode() != null ? conn.getTargetNode().getTitle() : null)
                .label(conn.getLabel())
                .build();
    }

    public SharedNodeResponse toSharedNodeResponse(SharedNode shared) {
        if (shared == null) return null;
        return SharedNodeResponse.builder()
                .id(shared.getId())
                .nodeId(shared.getNode() != null ? shared.getNode().getId() : null)
                .nodeTitle(shared.getNode() != null ? shared.getNode().getTitle() : null)
                .panelId(shared.getPanel() != null ? shared.getPanel().getId() : null)
                .panelSubjectName(shared.getPanel() != null ? shared.getPanel().getSubjectName() : null)
                .sharedById(shared.getSharedBy() != null ? shared.getSharedBy().getId() : null)
                .sharedByName(shared.getSharedBy() != null ? shared.getSharedBy().getFullName() : null)
                .permission(shared.getPermission() != null ? shared.getPermission() : shared.getPermissionLevel())
                .createdAt(shared.getCreatedAt())
                .build();
    }

    public AssignmentResponse toAssignmentResponse(Assignment assignment, int mcqCount) {
        if (assignment == null) return null;
        boolean expired = assignment.getDeadline() != null && assignment.getDeadline().isBefore(LocalDateTime.now());
        return AssignmentResponse.builder()
                .id(assignment.getId())
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .classroomId(assignment.getClassroom() != null ? assignment.getClassroom().getId() : null)
                .classroomName(assignment.getClassroom() != null ? assignment.getClassroom().getName() : null)
                .teacherId(assignment.getTeacher() != null ? assignment.getTeacher().getId() : null)
                .teacherName(assignment.getTeacher() != null ? assignment.getTeacher().getFullName() : null)
                .subject(assignment.getSubject())
                .deadline(assignment.getDeadline())
                .excalidrawTemplateData(assignment.getExcalidrawTemplateData())
                .extensionGrantedHours(assignment.getExtensionGrantedHours())
                .extensionReason(assignment.getExtensionReason())
                .mcqCount(mcqCount)
                .createdAt(assignment.getCreatedAt())
                .isExpired(expired)
                .build();
    }

    public MCQQuestionResponse toMCQQuestionResponse(MCQQuestion q) {
        if (q == null) return null;
        return MCQQuestionResponse.builder()
                .id(q.getId())
                .assignmentId(q.getAssignment() != null ? q.getAssignment().getId() : null)
                .questionNumber(q.getQuestionNumber())
                .questionText(q.getQuestionText())
                .optionA(q.getOptionA())
                .optionB(q.getOptionB())
                .optionC(q.getOptionC())
                .optionD(q.getOptionD())
                .correctOption(q.getCorrectOption())
                .explanation(q.getExplanation())
                .build();
    }

    public TabSwitchLogResponse toTabSwitchLogResponse(TabSwitchLog log) {
        if (log == null) return null;
        return TabSwitchLogResponse.builder()
                .id(log.getId())
                .switchedAt(log.getSwitchedAt())
                .reason(log.getReason())
                .build();
    }

    public SubmissionResponse toSubmissionResponse(Submission s, List<TabSwitchLog> logs) {
        if (s == null) return null;
        double mcq = s.getMcqScore() != null ? s.getMcqScore() : 0.0;
        double drawing = s.getDrawingScore() != null ? s.getDrawingScore() : 0.0;
        double total = mcq + drawing;

        List<TabSwitchLogResponse> logResponses = logs != null
                ? logs.stream().map(this::toTabSwitchLogResponse).toList()
                : List.of();

        return SubmissionResponse.builder()
                .id(s.getId())
                .assignmentId(s.getAssignment() != null ? s.getAssignment().getId() : null)
                .assignmentTitle(s.getAssignment() != null ? s.getAssignment().getTitle() : null)
                .assignmentDeadline(s.getAssignment() != null ? s.getAssignment().getDeadline() : null)
                .studentId(s.getStudent() != null ? s.getStudent().getId() : null)
                .studentName(s.getStudent() != null ? s.getStudent().getFullName() : null)
                .studentEmail(s.getStudent() != null ? s.getStudent().getEmail() : null)
                .studentRollNumber(s.getStudent() != null ? s.getStudent().getRollNumber() : null)
                .status(s.getStatus())
                .excalidrawDrawingData(s.getExcalidrawDrawingData())
                .tabSwitchCount(s.getTabSwitchCount())
                .mcqScore(s.getMcqScore())
                .drawingScore(s.getDrawingScore())
                .totalScore(total)
                .teacherFeedback(s.getTeacherFeedback())
                .submittedAt(s.getSubmittedAt())
                .gradedAt(s.getGradedAt())
                .gradedByName(s.getGradedBy() != null ? s.getGradedBy().getFullName() : null)
                .tabSwitchLogs(logResponses)
                .build();
    }

    public NoticeResponse toNoticeResponse(Notice notice) {
        if (notice == null) return null;
        return NoticeResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .targetRole(notice.getTargetRole())
                .collegeId(notice.getCollege() != null ? notice.getCollege().getId() : null)
                .classroomId(notice.getClassroom() != null ? notice.getClassroom().getId() : null)
                .classroomName(notice.getClassroom() != null ? notice.getClassroom().getName() : null)
                .authorId(notice.getAuthor() != null ? notice.getAuthor().getId() : null)
                .authorName(notice.getAuthor() != null ? notice.getAuthor().getFullName() : null)
                .createdAt(notice.getCreatedAt())
                .expiresAt(notice.getExpiresAt())
                .build();
    }

    public NewsItemResponse toNewsItemResponse(NewsItem item) {
        if (item == null) return null;
        return NewsItemResponse.builder()
                .id(item.getId())
                .title(item.getTitle())
                .summary(item.getSummary())
                .content(item.getContent())
                .sourceUrl(item.getSourceUrl())
                .imageUrl(item.getImageUrl())
                .category(item.getCategory())
                .authorId(item.getAuthor() != null ? item.getAuthor().getId() : null)
                .authorName(item.getAuthor() != null ? item.getAuthor().getFullName() : null)
                .createdAt(item.getCreatedAt())
                .build();
    }

    public ResearchDocumentResponse toResearchDocumentResponse(ResearchDocument doc) {
        if (doc == null) return null;
        return ResearchDocumentResponse.builder()
                .id(doc.getId())
                .title(doc.getTitle())
                .abstractText(doc.getAbstractText())
                .authors(doc.getAuthors())
                .journalOrConference(doc.getJournalOrConference())
                .publicationDate(doc.getPublicationDate())
                .documentFileUrl(doc.getDocumentFileUrl())
                .uploadedById(doc.getUploadedBy() != null ? doc.getUploadedBy().getId() : null)
                .uploadedByName(doc.getUploadedBy() != null ? doc.getUploadedBy().getFullName() : null)
                .createdAt(doc.getCreatedAt())
                .build();
    }

    public CalendarEventResponse toCalendarEventResponse(CalendarEvent event) {
        if (event == null) return null;
        return CalendarEventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventType(event.getEventType())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .collegeId(event.getCollege() != null ? event.getCollege().getId() : null)
                .collegeName(event.getCollege() != null ? event.getCollege().getName() : null)
                .isHoliday(event.getIsHoliday())
                .createdById(event.getCreatedBy() != null ? event.getCreatedBy().getId() : null)
                .createdByName(event.getCreatedBy() != null ? event.getCreatedBy().getFullName() : null)
                .createdAt(event.getCreatedAt())
                .build();
    }

    public TicketResponse toTicketResponse(Ticket ticket) {
        if (ticket == null) return null;
        return TicketResponse.builder()
                .id(ticket.getId())
                .type(ticket.getType())
                .status(ticket.getStatus())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .createdById(ticket.getCreatedBy() != null ? ticket.getCreatedBy().getId() : null)
                .createdByName(ticket.getCreatedBy() != null ? ticket.getCreatedBy().getFullName() : null)
                .createdByEmail(ticket.getCreatedBy() != null ? ticket.getCreatedBy().getEmail() : null)
                .targetUserId(ticket.getTargetUser() != null ? ticket.getTargetUser().getId() : null)
                .targetUserName(ticket.getTargetUser() != null ? ticket.getTargetUser().getFullName() : null)
                .requestedChanges(ticket.getRequestedChanges())
                .assignedToId(ticket.getAssignedTo() != null ? ticket.getAssignedTo().getId() : null)
                .assignedToName(ticket.getAssignedTo() != null ? ticket.getAssignedTo().getFullName() : null)
                .resolutionNotes(ticket.getResolutionNotes())
                .createdAt(ticket.getCreatedAt())
                .resolvedAt(ticket.getResolvedAt())
                .build();
    }
}