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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private static final long REPORT_STREAM_TIMEOUT = 180_000L;

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
    public ReportResponseDto findReportById(String username, Long reportId) {
        User user = getUserByUsername(username);

        Report report = reportRepository.findByIdAndOwner(reportId, user)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "보고서를 찾을 수 없습니다."));

        return ReportResponseDto.toResponseDto(report);
    }

    @Override
    public SseEmitter createReport(String username, LocalDate startDate, LocalDate endDate) {
        User user = getUserByUsername(username);

        if (reportRepository.existsByOwnerAndStartDateAndEndDate(user, startDate, endDate)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "이미 해당 기간의 보고서가 존재합니다.");
        }

        ReportAiRequestDto aiRequest = buildAiRequest(user, startDate, endDate);

        String title = startDate.getYear() + "년 " + endDate.getMonthValue()+"월" + " 수입 · 지출 내역 분석 보고서";

        Report report = reportRepository.save(Report.builder()
                .title(title)
                .content("")
                .startDate(startDate)
                .endDate(endDate)
                .owner(user)
                .build());

        SseEmitter emitter = new SseEmitter(REPORT_STREAM_TIMEOUT);
        StringBuilder contentBuilder = new StringBuilder();
        AtomicReference<Disposable> subscriptionRef = new AtomicReference<>();
        Runnable disposeSubscription = () -> {
            Disposable subscription = subscriptionRef.get();
            if (subscription != null && !subscription.isDisposed()) {
                subscription.dispose();
            }
        };

        emitter.onCompletion(disposeSubscription);
        emitter.onTimeout(() -> {
            disposeSubscription.run();
            emitter.complete();
        });
        emitter.onError((e) -> disposeSubscription.run());

        sendEvent(emitter, "start", ReportResponseDto.toResponseDto(report));

        Disposable subscription = openAiService.streamReportResponse(aiRequest)
                .subscribe(
                        content -> {
                            contentBuilder.append(content);
                            sendEvent(emitter, "content", content);
                        },
                        e -> {
                            log.error("보고서 스트리밍 생성 중 오류가 발생했습니다.", e);
                            sendErrorEvent(emitter, e);
                        },
                        () -> {
                            try {
                                report.updateContent(contentBuilder.toString());
                                Report savedReport = reportRepository.save(report);
                                sendEvent(emitter, "complete", ReportResponseDto.toResponseDto(savedReport));
                                emitter.complete();
                            } catch (Exception e) {
                                log.error("보고서 저장 중 오류가 발생했습니다.", e);
                                sendErrorEvent(emitter, e);
                            }
                        }
                );
        subscriptionRef.set(subscription);

        return emitter;
    }

    private void sendEvent(SseEmitter emitter, String name, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .name(name)
                    .data(data));
        } catch (IOException e) {
            throw new IllegalStateException("SSE 이벤트 전송 중 오류가 발생했습니다.", e);
        }
    }

    private void sendErrorEvent(SseEmitter emitter, Throwable e) {
        String message = e.getMessage() != null ? e.getMessage() : "보고서 생성 중 오류가 발생했습니다.";

        try {
            emitter.send(SseEmitter.event()
                    .name("error")
                    .data(message));
        } catch (IOException ignored) {
            log.debug("SSE 오류 이벤트 전송에 실패했습니다.", ignored);
        } finally {
            emitter.complete();
        }
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
