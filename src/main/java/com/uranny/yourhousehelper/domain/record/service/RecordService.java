package com.uranny.yourhousehelper.domain.record.service;

import com.uranny.yourhousehelper.domain.record.dto.request.RecordCreateRequestDto;
import com.uranny.yourhousehelper.domain.record.dto.request.RecordUpdateRequestDto;
import com.uranny.yourhousehelper.domain.record.dto.response.RecordResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface RecordService {
    RecordResponseDto createRecord(String username, RecordCreateRequestDto recordCreateRequestDto);

    List<RecordResponseDto> findRecordsByUserAndDateBetweenOrderByDateDesc(String username, LocalDate startDate, LocalDate endDate);

    RecordResponseDto updateRecordById(String username, Long id, RecordUpdateRequestDto recordUpdateRequestDto);

    void deleteRecordById(String username, Long id);
}
