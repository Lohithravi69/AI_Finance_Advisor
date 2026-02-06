package com.aifa.finance.repository;

import com.aifa.finance.domain.SyncLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SyncLogRepository extends JpaRepository<SyncLog, Long> {

    @Query("SELECT s FROM SyncLog s WHERE s.user.id = :userId ORDER BY s.createdAt DESC")
    List<SyncLog> findByUserId(Long userId);

    @Query("SELECT s FROM SyncLog s WHERE s.user.id = :userId AND s.syncStatus = :status " +
           "ORDER BY s.createdAt DESC")
    List<SyncLog> findByUserIdAndStatus(Long userId, SyncLog.SyncStatus status);

    @Query("SELECT s FROM SyncLog s WHERE s.user.id = :userId AND s.syncType = :type " +
           "ORDER BY s.completedAt DESC NULLS LAST")
    List<SyncLog> findByUserIdAndType(Long userId, SyncLog.SyncType type);

    @Query("SELECT s FROM SyncLog s WHERE s.user.id = :userId AND s.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY s.createdAt DESC")
    List<SyncLog> findByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT SUM(s.itemsSynced) FROM SyncLog s WHERE s.user.id = :userId AND s.syncStatus = :status")
    Integer sumItemsSyncedByStatus(Long userId, SyncLog.SyncStatus status);

       SyncLog findTopByUserIdOrderByCompletedAtDesc(Long userId);

    @Query("SELECT COUNT(s) FROM SyncLog s WHERE s.user.id = :userId AND s.conflictsDetected > 0")
    long countSyncsWithConflicts(Long userId);
}
