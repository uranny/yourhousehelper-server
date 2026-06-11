package com.uranny.yourhousehelper.domain.report.service;

import com.uranny.yourhousehelper.domain.report.dto.response.ReportResponseDto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    List<ReportResponseDto> findReportsByUser(String username);
    ReportResponseDto findReportById(String username, Long reportId);
    SseEmitter createReport(String username, LocalDate startDate, LocalDate endDate);
}
