package com.example.EduGraph.controller;

import com.example.EduGraph.dto.request.CalendarEventRequest;
import com.example.EduGraph.dto.response.ApiResponse;
import com.example.EduGraph.dto.response.CalendarEventResponse;
import com.example.EduGraph.security.SecurityUserPrincipal;
import com.example.EduGraph.service.CalendarService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendar")

public class CalendarController {

    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PRINCIPAL')")
    public ResponseEntity<ApiResponse<CalendarEventResponse>> createEvent(
            @Valid @RequestBody CalendarEventRequest request,
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(calendarService.createEvent(request, principal.getId()), "Calendar event created"));
    }

    @GetMapping("/college/{collegeId}")
    public ResponseEntity<ApiResponse<List<CalendarEventResponse>>> getEventsForCollege(@PathVariable Long collegeId) {
        return ResponseEntity.ok(ApiResponse.success(calendarService.getEventsForCollege(collegeId)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PRINCIPAL')")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(
            @PathVariable Long id,
            @AuthenticationPrincipal SecurityUserPrincipal principal) {
        calendarService.deleteEvent(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Calendar event deleted"));
    }
}
