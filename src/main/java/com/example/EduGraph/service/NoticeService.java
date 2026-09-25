package com.example.EduGraph.service;

import com.example.EduGraph.dto.request.NoticeRequest;
import com.example.EduGraph.dto.response.NoticeResponse;

import java.util.List;

public interface NoticeService {
    NoticeResponse createNotice(NoticeRequest request, Long authorId);
    List<NoticeResponse> getNoticesForUser(Long userId);
    List<NoticeResponse> getNoticesByCollege(Long collegeId);
    void deleteNotice(Long noticeId, Long authorId);
}
