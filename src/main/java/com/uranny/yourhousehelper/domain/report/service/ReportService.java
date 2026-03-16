package com.uranny.yourhousehelper.domain.report.service;

import com.uranny.yourhousehelper.domain.report.dto.response.ReportResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    List<ReportResponseDto> findReportsByUser(String username);

    ReportResponseDto createReport(String username, LocalDate startDate, LocalDate endDate);
}
