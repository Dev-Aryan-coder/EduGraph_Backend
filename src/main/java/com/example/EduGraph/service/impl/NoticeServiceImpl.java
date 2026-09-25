package com.example.EduGraph.service.impl;

import com.example.EduGraph.dto.request.NoticeRequest;
import com.example.EduGraph.dto.response.NoticeResponse;
import com.example.EduGraph.entity.Classroom;
import com.example.EduGraph.entity.College;
import com.example.EduGraph.entity.Notice;
import com.example.EduGraph.entity.User;
import com.example.EduGraph.enums.UserRole;
import com.example.EduGraph.exception.ResourceNotFoundException;
import com.example.EduGraph.exception.UnauthorizedAccessException;
import com.example.EduGraph.mapper.EntityMapper;
import com.example.EduGraph.repository.ClassroomRepository;
import com.example.EduGraph.repository.CollegeRepository;
import com.example.EduGraph.repository.NoticeRepository;
import com.example.EduGraph.repository.UserRepository;
import com.example.EduGraph.service.NoticeService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service

@Slf4j
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final CollegeRepository collegeRepository;
    private final ClassroomRepository classroomRepository;
    private final EntityMapper entityMapper;

    public NoticeServiceImpl(NoticeRepository noticeRepository, UserRepository userRepository, CollegeRepository collegeRepository, ClassroomRepository classroomRepository, EntityMapper entityMapper) {
        this.noticeRepository = noticeRepository;
        this.userRepository = userRepository;
        this.collegeRepository = collegeRepository;
        this.classroomRepository = classroomRepository;
        this.entityMapper = entityMapper;
    }


    @Override
    @Transactional
    public NoticeResponse createNotice(NoticeRequest request, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));

        College college = null;
        if (request.getCollegeId() != null) {
            college = collegeRepository.findById(request.getCollegeId()).orElse(null);
        } else if (author.getCollege() != null) {
            college = author.getCollege();
        }

        Classroom classroom = null;
        if (request.getClassroomId() != null) {
            classroom = classroomRepository.findById(request.getClassroomId()).orElse(null);
        }

        Notice notice = Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .targetRole(request.getTargetRole())
                .college(college)
                .classroom(classroom)
                .author(author)
                .expiresAt(request.getExpiresAt())
                .build();

        Notice saved = noticeRepository.save(notice);
        return entityMapper.toNoticeResponse(saved);
    }

    @Override
    public List<NoticeResponse> getNoticesForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Long collegeId = user.getCollege() != null ? user.getCollege().getId() : null;
        Long classroomId = user.getClassroom() != null ? user.getClassroom().getId() : null;

        List<Notice> notices;
        if (collegeId != null && classroomId != null) {
            notices = noticeRepository.findByCollegeIdOrClassroomId(collegeId, classroomId);
        } else if (collegeId != null) {
            notices = noticeRepository.findByCollegeId(collegeId);
        } else {
            notices = noticeRepository.findAll();
        }

        return notices.stream()
                .filter(n -> n.getTargetRole() == null || n.getTargetRole() == user.getRole())
                .map(entityMapper::toNoticeResponse)
                .toList();
    }

    @Override
    public List<NoticeResponse> getNoticesByCollege(Long collegeId) {
        return noticeRepository.findByCollegeId(collegeId).stream()
                .map(entityMapper::toNoticeResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteNotice(Long noticeId, Long authorId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("Notice not found"));

        User user = userRepository.findById(authorId).orElseThrow();
        if (!notice.getAuthor().getId().equals(authorId) && user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.PRINCIPAL) {
            throw new UnauthorizedAccessException("Not authorized to remove this notice");
        }
        noticeRepository.delete(notice);
    }
}