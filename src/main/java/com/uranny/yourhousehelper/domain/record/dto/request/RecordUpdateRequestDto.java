package com.uranny.yourhousehelper.domain.record.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.uranny.yourhousehelper.domain.record.enums.RecordType;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecordUpdateRequestDto {
    private RecordType recordType;

    private Integer cost;

    private String description;

    private LocalDate date;
}
