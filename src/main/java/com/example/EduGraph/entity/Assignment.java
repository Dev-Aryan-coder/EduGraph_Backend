package com.example.EduGraph.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assignment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", nullable = false)
    private Classroom classroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Column(length = 100)
    private String subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_image_id")
    private UploadedFile referenceImage;

    @Lob
    @Column(name = "excalidraw_template_data", columnDefinition = "LONGTEXT")
    private String excalidrawTemplateData;

    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Column(name = "extension_granted_hours")
    @Builder.Default
    private Integer extensionGrantedHours = 0;

    @Column(name = "extension_reason", length = 500)
    private String extensionReason;

    @Column(name = "max_marks")
    @Builder.Default
    private Integer maxMarks = 30;

    @Column(name = "copy_paste_allowed")
    @Builder.Default
    private Boolean copyPasteAllowed = false;
}