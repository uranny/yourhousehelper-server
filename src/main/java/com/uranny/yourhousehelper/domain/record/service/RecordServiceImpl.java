package com.uranny.yourhousehelper.domain.record.service;

import com.uranny.yourhousehelper.domain.record.dto.request.RecordCreateRequestDto;
import com.uranny.yourhousehelper.domain.record.dto.request.RecordUpdateRequestDto;
import com.uranny.yourhousehelper.domain.record.dto.response.RecordResponseDto;
import com.uranny.yourhousehelper.domain.record.entity.Record;
import com.uranny.yourhousehelper.domain.record.repository.RecordRepository;
import com.uranny.yourhousehelper.domain.user.entity.User;
import com.uranny.yourhousehelper.domain.user.repository.UserRepository;
import com.uranny.yourhousehelper.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.uranny.yourhousehelper.domain.record.dto.response.RecordResponseDto.toResponseDto;

@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {
    private final RecordRepository recordRepository;
    private final UserRepository userRepository;

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
    }

    @Override
    public void createRecord(String username, RecordCreateRequestDto recordDto) {
        User user = getUserByUsername(username);
        Record record = recordDto.toEntity(user);
        Record savedRecord = recordRepository.save(record);

        toResponseDto(savedRecord);
    }

    @Override
    public List<RecordResponseDto> findRecordsByUserAndDateBetweenOrderByDateDesc(String username, LocalDate startDate, LocalDate endDate) {
        User user = getUserByUsername(username);
        List<Record> records = recordRepository.findRecordsByOwnerAndDateBetweenOrderByDateDesc(user, startDate, endDate);
        return records
                .stream()
                .map(RecordResponseDto::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public RecordResponseDto updateRecordById(String username, Long id, RecordUpdateRequestDto recordDto) {
        Record existsRecord = recordRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "수정할 기록이 존재하지 않습니다"));

        User user = getUserByUsername(username);

        if (!Objects.equals(existsRecord.getOwner().getId(), user.getId())) {
            throw new CustomException(HttpStatus.FORBIDDEN, "기록에 대한 권한이 없습니다");
        }

        existsRecord.update(recordDto);

        Record savedRecord = recordRepository.save(existsRecord);
        return toResponseDto(savedRecord);
    }

    @Override
    public void deleteRecordById(String username, Long id) {
        Record record = recordRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "삭제할 기록이 존재하지 않습니다"));

        User user = getUserByUsername(username);

        if (!Objects.equals(record.getOwner().getId(), user.getId())) {
            throw new CustomException(HttpStatus.FORBIDDEN, "기록에 대한 권한이 없습니다");
        }

        recordRepository.deleteById(id);
    }
}
