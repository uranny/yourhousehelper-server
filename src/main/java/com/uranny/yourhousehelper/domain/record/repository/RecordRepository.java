package com.uranny.yourhousehelper.domain.record.repository;

import com.uranny.yourhousehelper.domain.record.entity.Record;
import com.uranny.yourhousehelper.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {

    List<Record> findRecordsByOwnerAndDateBetweenOrderByDateDesc(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );

    // 전체 cost 합계
    @Query("""
        SELECT COALESCE(SUM(r.cost),0)
        FROM Record r
        WHERE r.owner = :user
        AND r.date BETWEEN :startDate AND :endDate
    """)
    Long sumCostByUserAndDateBetween(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );

    // INCOME cost 합계
    @Query("""
        SELECT COALESCE(SUM(r.cost),0)
        FROM Record r
        WHERE r.owner = :user
        AND r.recordType = 'INCOME'
        AND r.date BETWEEN :startDate AND :endDate
    """)
    Long sumIncomeByUserAndDateBetween(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );

    // EXPENSE cost 합계
    @Query("""
        SELECT COALESCE(SUM(r.cost),0)
        FROM Record r
        WHERE r.owner = :user
        AND r.recordType = 'EXPENSE'
        AND r.date BETWEEN :startDate AND :endDate
    """)
    Long sumExpenseByUserAndDateBetween(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );

    // INCOME description 중복 제거
    @Query("""
        SELECT DISTINCT r.description
        FROM Record r
        WHERE r.owner = :user
        AND r.recordType = 'INCOME'
        AND r.date BETWEEN :startDate AND :endDate
    """)
    List<String> findDistinctIncomeDescriptions(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );

    // EXPENSE description 중복 제거
    @Query("""
        SELECT DISTINCT r.description
        FROM Record r
        WHERE r.owner = :user
        AND r.recordType = 'EXPENSE'
        AND r.date BETWEEN :startDate AND :endDate
    """)
    List<String> findDistinctExpenseDescriptions(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );
    @Query("""
        SELECT r.description
        FROM Record r
        WHERE r.owner = :user
        AND r.recordType = 'EXPENSE'
        AND r.date BETWEEN :startDate AND :endDate
        GROUP BY r.description
        ORDER BY SUM(r.cost) DESC
    """)
    List<String> findTopExpenseCategories(User user, LocalDate startDate, LocalDate endDate);
}