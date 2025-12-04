package com.uranny.yourhousehelper.domain.record.dto.request;

import com.uranny.yourhousehelper.domain.record.enums.RecordType;
import com.uranny.yourhousehelper.domain.record.entity.Record;
import com.uranny.yourhousehelper.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class RecordCreateRequestDto {

    @NotNull(message = "기록 타입은 필수입니다")
    private RecordType recordType;

    @NotNull(message = "금액은 필수입니다")
    private Integer cost;

    @NotBlank(message = "사유 필수입니다")
    private String description;

    @NotNull(message = "날짜는 필수입니다")
    private LocalDate date;

    public Record toEntity(User user) {
        return Record.builder()
                .recordType(recordType)
                .cost(cost)
                .description(description)
                .date(date)
                .owner(user)
                .build();
    }
}
