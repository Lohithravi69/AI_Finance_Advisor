package com.aifa.finance.repository;

import com.aifa.finance.domain.DataImport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DataImportRepository extends JpaRepository<DataImport, Long> {

    @Query("SELECT d FROM DataImport d WHERE d.user.id = :userId ORDER BY d.createdAt DESC")
    List<DataImport> findByUserId(Long userId);

    @Query("SELECT d FROM DataImport d WHERE d.user.id = :userId AND d.importStatus = :status " +
           "ORDER BY d.createdAt DESC")
    List<DataImport> findByUserIdAndStatus(Long userId, DataImport.ImportStatus status);

    @Query("SELECT d FROM DataImport d WHERE d.user.id = :userId AND d.importType = :type " +
           "ORDER BY d.createdAt DESC")
    List<DataImport> findByUserIdAndType(Long userId, DataImport.ImportType type);

    @Query("SELECT d FROM DataImport d WHERE d.user.id = :userId AND d.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY d.createdAt DESC")
    List<DataImport> findByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT SUM(d.successCount) FROM DataImport d WHERE d.user.id = :userId")
    Integer sumSuccessfulImports(Long userId);

    @Query("SELECT COUNT(d) FROM DataImport d WHERE d.user.id = :userId AND d.importStatus = :status")
    long countByUserIdAndStatus(Long userId, DataImport.ImportStatus status);
}
