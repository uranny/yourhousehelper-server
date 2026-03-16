package com.uranny.yourhousehelper.domain.report.repository;

import com.uranny.yourhousehelper.domain.report.entity.Report;
import com.uranny.yourhousehelper.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findAllByOwner(User owner);
    boolean existsByOwnerAndStartDateAndEndDate(User owner, LocalDate startDate, LocalDate endDate);
}
