package com.uranny.yourhousehelper.domain.record.controller;

import com.uranny.yourhousehelper.domain.record.dto.request.RecordCreateRequestDto;
import com.uranny.yourhousehelper.domain.record.dto.request.RecordUpdateRequestDto;
import com.uranny.yourhousehelper.domain.record.dto.response.RecordResponseDto;
import com.uranny.yourhousehelper.domain.record.service.RecordService;
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
@RequestMapping("/record")
@RequiredArgsConstructor
public class RecordController {
    private final RecordService recordService;

    @PostMapping
    public ResponseEntity<BaseResponse<RecordResponseDto>> createRecord(
            Principal principal,
            @Valid
            @RequestBody
            RecordCreateRequestDto recordCreateRequestDto
    ) {
        recordService.createRecord(principal.getName(), recordCreateRequestDto);
        return null;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<RecordResponseDto>>> findRecordsByDate(
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
        List<RecordResponseDto> results = recordService.findRecordsByUserAndDateBetweenOrderByDateDesc(principal.getName(), startDate, endDate);
        return BaseResponse.of(results, HttpStatus.OK, "기록 목록 조회에 성공했습니다");
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<RecordResponseDto>> updateRecord(
            Principal principal,
            @PathVariable
            Long id,
            @Valid
            @RequestBody
            RecordUpdateRequestDto recordUpdateRequestDto
    ) {
        RecordResponseDto result = recordService.updateRecordById(principal.getName(), id, recordUpdateRequestDto);
        return BaseResponse.of(result, HttpStatus.OK, "기록 수정에 성공했습니다");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteRecord(
            Principal principal,
            @PathVariable
            Long id
    ) {
        recordService.deleteRecordById(principal.getName(), id);
        return BaseResponse.of(HttpStatus.OK, "기록 삭제에 성공했습니다");
    }
}
