package com.example.EduGraph.service.impl;

import com.example.EduGraph.dto.request.CalendarEventRequest;
import com.example.EduGraph.dto.response.CalendarEventResponse;
import com.example.EduGraph.entity.CalendarEvent;
import com.example.EduGraph.entity.College;
import com.example.EduGraph.entity.User;
import com.example.EduGraph.enums.UserRole;
import com.example.EduGraph.exception.ResourceNotFoundException;
import com.example.EduGraph.exception.UnauthorizedAccessException;
import com.example.EduGraph.mapper.EntityMapper;
import com.example.EduGraph.repository.CalendarEventRepository;
import com.example.EduGraph.repository.CollegeRepository;
import com.example.EduGraph.repository.UserRepository;
import com.example.EduGraph.service.CalendarService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service

@Slf4j
public class CalendarServiceImpl implements CalendarService {

    private final CalendarEventRepository calendarEventRepository;
    private final CollegeRepository collegeRepository;
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;

    public CalendarServiceImpl(CalendarEventRepository calendarEventRepository, CollegeRepository collegeRepository, UserRepository userRepository, EntityMapper entityMapper) {
        this.calendarEventRepository = calendarEventRepository;
        this.collegeRepository = collegeRepository;
        this.userRepository = userRepository;
        this.entityMapper = entityMapper;
    }


    @Override
    @Transactional
    public CalendarEventResponse createEvent(CalendarEventRequest request, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        College college = null;
        if (request.getCollegeId() != null) {
            college = collegeRepository.findById(request.getCollegeId()).orElse(null);
        } else if (creator.getCollege() != null) {
            college = creator.getCollege();
        }

        CalendarEvent event = CalendarEvent.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .eventType(request.getEventType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate() != null ? request.getEndDate() : request.getStartDate())
                .college(college)
                .isHoliday(Boolean.TRUE.equals(request.getIsHoliday()))
                .createdBy(creator)
                .build();

        CalendarEvent saved = calendarEventRepository.save(event);
        return entityMapper.toCalendarEventResponse(saved);
    }

    @Override
    public List<CalendarEventResponse> getEventsForCollege(Long collegeId) {
        List<CalendarEvent> events;
        if (collegeId != null) {
            events = calendarEventRepository.findByCollegeId(collegeId);
        } else {
            events = calendarEventRepository.findAll();
        }
        return events.stream().map(entityMapper::toCalendarEventResponse).toList();
    }

    @Override
    @Transactional
    public void deleteEvent(Long eventId, Long userId) {
        CalendarEvent event = calendarEventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Calendar event not found"));
        User user = userRepository.findById(userId).orElseThrow();
        if (!event.getCreatedBy().getId().equals(userId) && user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.PRINCIPAL) {
            throw new UnauthorizedAccessException("Not authorized to remove this calendar entry");
        }
        calendarEventRepository.delete(event);
    }
}