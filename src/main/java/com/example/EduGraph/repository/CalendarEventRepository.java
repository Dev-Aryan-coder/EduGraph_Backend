package com.example.EduGraph.repository;

import com.example.EduGraph.entity.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {

    List<CalendarEvent> findByCollegeIdAndStartDateBetween(Long collegeId, LocalDate fromDate, LocalDate toDate);

    List<CalendarEvent> findByCollegeId(Long collegeId);
}