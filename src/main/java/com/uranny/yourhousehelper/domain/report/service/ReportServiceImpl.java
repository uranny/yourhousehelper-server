package com.uranny.yourhousehelper.domain.report.service;

import com.uranny.yourhousehelper.domain.record.repository.RecordRepository;
import com.uranny.yourhousehelper.external.openai.dto.request.ReportAiRequestDto;
import com.uranny.yourhousehelper.domain.report.dto.response.ReportResponseDto;
import com.uranny.yourhousehelper.domain.report.entity.Report;
import com.uranny.yourhousehelper.domain.report.repository.ReportRepository;
import com.uranny.yourhousehelper.domain.user.entity.User;
import com.uranny.yourhousehelper.domain.user.repository.UserRepository;
import com.uranny.yourhousehelper.external.openai.service.OpenAiService;
import com.uranny.yourhousehelper.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final RecordRepository recordRepository;
    private final UserRepository userRepository;
    private final OpenAiService openAiService;

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
    }

    @Override
    public List<ReportResponseDto> findReportsByUser(String username) {
        User user = getUserByUsername(username);
        List<Report> reports = reportRepository.findAllByOwner(user);
        return reports
                .stream()
                .map(ReportResponseDto::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReportResponseDto createReport(String username, LocalDate startDate, LocalDate endDate) {
        User user = getUserByUsername(username);

        if (reportRepository.existsByOwnerAndStartDateAndEndDate(user, startDate, endDate)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "이미 해당 기간의 보고서가 존재합니다.");
        }

        ReportAiRequestDto aiRequest = buildAiRequest(user, startDate, endDate);

        String content = openAiService.getReportResponse(aiRequest);

        String title = startDate.getYear() + "년 " + endDate.getMonthValue()+"월" + " 수입 · 지출 내역 분석 보고서";

        Report report = Report.builder()
                        .title(title)
                        .content(content)
                        .startDate(startDate)
                        .endDate(endDate)
                        .owner(user)
                        .build();

        reportRepository.save(report);

        return ReportResponseDto.toResponseDto(report);
    }
    private ReportAiRequestDto buildAiRequest(User user, LocalDate startDate, LocalDate endDate) {

        Long total = recordRepository.sumCostByUserAndDateBetween(user, startDate, endDate);
        Long income = recordRepository.sumIncomeByUserAndDateBetween(user, startDate, endDate);
        Long expense = recordRepository.sumExpenseByUserAndDateBetween(user, startDate, endDate);

        String keyExpenses = String.join(", ",
                recordRepository.findDistinctExpenseDescriptions(user, startDate, endDate));

        String keyIncomes = String.join(", ",
                recordRepository.findDistinctIncomeDescriptions(user, startDate, endDate));

        String topExpenses = String.join(", ",
                recordRepository.findTopExpenseCategories(user, startDate, endDate));

        // 이전 달 계산
        LocalDate pastStart = startDate.minusMonths(1);
        LocalDate pastEnd = endDate.minusMonths(1);

        return ReportAiRequestDto.builder()
                .reason(user.getReason())
                .finalMoney(user.getFinalMoney())

                .totalMoney(total)
                .totalExpenseMoney(expense)
                .totalIncomeMoney(income)
                .keyExpenses(keyExpenses)
                .keyIncomes(keyIncomes)
                .topExpenses(topExpenses)

                .pastTotalMoney(recordRepository.sumCostByUserAndDateBetween(user, pastStart, pastEnd))
                .pastTotalExpenseMoney(recordRepository.sumExpenseByUserAndDateBetween(user, pastStart, pastEnd))
                .pastTotalIncomeMoney(recordRepository.sumIncomeByUserAndDateBetween(user, pastStart, pastEnd))
                .pastKeyExpenses(String.join(", ",
                        recordRepository.findDistinctExpenseDescriptions(user, pastStart, pastEnd)))
                .pastKeyIncomes(String.join(", ",
                        recordRepository.findDistinctIncomeDescriptions(user, pastStart, pastEnd)))

                .build();
    }
}
