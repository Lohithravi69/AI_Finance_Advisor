package com.aifa.finance.repository;

import com.aifa.finance.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByUserIdOrderByGeneratedAtDesc(Long userId);

    List<Report> findByUserIdAndReportTypeOrderByGeneratedAtDesc(Long userId, Report.ReportType reportType);

    @Query("SELECT r FROM Report r WHERE r.user.id = :userId AND r.generatedAt BETWEEN :startDate AND :endDate ORDER BY r.generatedAt DESC")
    List<Report> findByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT r FROM Report r WHERE r.user.id = :userId AND r.isScheduled = true AND r.nextGeneration <= :currentDateTime")
    List<Report> findScheduledReportsDue(Long userId, LocalDateTime currentDateTime);

    List<Report> findByUserIdAndIsScheduledTrue(Long userId);

    @Query("SELECT COUNT(r) FROM Report r WHERE r.user.id = :userId")
    Long countByUserId(Long userId);

    void deleteByUserIdAndId(Long userId, Long reportId);
}
