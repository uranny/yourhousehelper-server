package com.uranny.yourhousehelper.domain.record.entity;

import com.uranny.yourhousehelper.domain.record.dto.request.RecordUpdateRequestDto;
import com.uranny.yourhousehelper.domain.record.enums.RecordType;
import com.uranny.yourhousehelper.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Entity
@Table(name = "records")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecordType recordType;

    @Column(nullable = false)
    private Integer cost;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

    public void update(RecordUpdateRequestDto dto) {
        if (dto.getRecordType() != null) {
            this.recordType = dto.getRecordType();
        }
        if (dto.getCost() != null) {
            this.cost = dto.getCost();
        }
        if (dto.getDescription() != null && StringUtils.hasText(dto.getDescription().trim())) {
            this.description = dto.getDescription();
        }
        if (dto.getDate() != null) {
            this.date = dto.getDate();
        }
    }
}
