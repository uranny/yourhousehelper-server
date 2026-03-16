package com.uranny.yourhousehelper.domain.report.dto.response;

import com.uranny.yourhousehelper.domain.report.entity.Report;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponseDto {
    private Long id;

    private String title;

    private String content;

    private LocalDate fromDate;

    private LocalDate toDate;

    public static ReportResponseDto toResponseDto(Report report) {
        return new ReportResponseDto(
                report.getId(),
                report.getTitle(),
                report.getContent(),
                report.getStartDate(),
                report.getEndDate()
        );
    }
}
