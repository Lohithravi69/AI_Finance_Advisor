package com.aifa.finance.repository;

import com.aifa.finance.domain.AnalyticsSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnalyticsSnapshotRepository extends JpaRepository<AnalyticsSnapshot, Long> {

    Optional<AnalyticsSnapshot> findByUserIdAndSnapshotDate(Long userId, LocalDate snapshotDate);

    List<AnalyticsSnapshot> findByUserIdOrderBySnapshotDateDesc(Long userId);

    @Query("SELECT a FROM AnalyticsSnapshot a WHERE a.user.id = :userId AND a.snapshotDate BETWEEN :startDate AND :endDate ORDER BY a.snapshotDate ASC")
    List<AnalyticsSnapshot> findByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT a FROM AnalyticsSnapshot a WHERE a.user.id = :userId ORDER BY a.snapshotDate DESC LIMIT 30")
    List<AnalyticsSnapshot> findLast30Days(Long userId);

    @Query("SELECT a FROM AnalyticsSnapshot a WHERE a.user.id = :userId ORDER BY a.snapshotDate DESC LIMIT 1")
    Optional<AnalyticsSnapshot> findLatestSnapshot(Long userId);

    void deleteByUserIdAndSnapshotDateBefore(Long userId, LocalDate cutoffDate);
}
