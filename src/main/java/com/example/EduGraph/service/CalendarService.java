package com.example.EduGraph.service;

import com.example.EduGraph.dto.request.CalendarEventRequest;
import com.example.EduGraph.dto.response.CalendarEventResponse;

import java.util.List;

public interface CalendarService {
    CalendarEventResponse createEvent(CalendarEventRequest request, Long creatorId);
    List<CalendarEventResponse> getEventsForCollege(Long collegeId);
    void deleteEvent(Long eventId, Long userId);
}
