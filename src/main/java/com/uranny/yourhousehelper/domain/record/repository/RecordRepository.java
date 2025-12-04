package com.uranny.yourhousehelper.domain.record.repository;

import com.uranny.yourhousehelper.domain.record.entity.Record;
import com.uranny.yourhousehelper.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findRecordsByOwnerAndDateBetweenOrderByDateDesc(User user, LocalDate startDate, LocalDate endDate);
}
