package com.uranny.yourhousehelper.domain.record.dto.response;

import com.uranny.yourhousehelper.domain.record.entity.Record;
import com.uranny.yourhousehelper.domain.record.enums.RecordType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordResponseDto {
    private Long id;

    private RecordType recordType;

    private Integer cost;

    private String description;

    private LocalDate date;

    public static RecordResponseDto toResponseDto(Record record) {
        return new RecordResponseDto(
                record.getId(),
                record.getRecordType(),
                record.getCost(),
                record.getDescription(),
                record.getDate()
        );
    }
}
