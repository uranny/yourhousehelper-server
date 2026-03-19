package com.uranny.yourhousehelper.domain.report.controller;

import com.uranny.yourhousehelper.domain.report.dto.response.ReportResponseDto;
import com.uranny.yourhousehelper.domain.report.entity.Report;
import com.uranny.yourhousehelper.domain.report.service.ReportService;
import com.uranny.yourhousehelper.global.common.BaseResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<ReportResponseDto>>> findReports(
            Principal principal
    ) {
        List<ReportResponseDto> results = reportService.findReportsByUser(principal.getName());
        return BaseResponse.of(results, HttpStatus.OK, "분석 보고서 조회에 성공했습니다");
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ReportResponseDto>> findReportById(
            Principal principal,
            @PathVariable Long id
    ) {
        ReportResponseDto result = reportService.findReportById(principal.getName(),id);
        return BaseResponse.of(result, HttpStatus.OK, "분석 보고서 단건 조회에 성공했습니다");
    }

    @PostMapping
    public ResponseEntity<BaseResponse<ReportResponseDto>> createReport(
            Principal principal,
            @RequestParam
            @NotNull(message = "시작일은 필수입니다")
            @Valid
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate startDate,
            @RequestParam
            @NotNull(message = "마감일은 필수입니다")
            @Valid
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ){
        ReportResponseDto result = reportService.createReport(principal.getName(), startDate, endDate);
        return BaseResponse.of(result, HttpStatus.OK, "분석 보고서 생성에 성공했습니다");
    }
}
