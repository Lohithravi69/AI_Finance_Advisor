package com.aifa.finance.service;

import com.aifa.finance.domain.SyncLog;
import com.aifa.finance.domain.User;
import com.aifa.finance.dto.SyncLogRequest;
import com.aifa.finance.dto.SyncLogResponse;
import com.aifa.finance.exception.ResourceNotFoundException;
import com.aifa.finance.repository.SyncLogRepository;
import com.aifa.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SyncService {

    private final SyncLogRepository syncLogRepository;
    private final UserRepository userRepository;

    @Transactional
    public SyncLogResponse createSync(Long userId, SyncLogRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        SyncLog syncLog = SyncLog.builder()
            .user(user)
            .syncType(SyncLog.SyncType.valueOf(request.syncType()))
            .syncStatus(SyncLog.SyncStatus.PENDING)
            .sourceSystem(request.sourceSystem())
            .resolutionStrategy(request.resolutionStrategy())
            .syncNotes(request.syncNotes())
            .itemsSynced(0)
            .conflictsDetected(0)
            .conflictsResolved(0)
            .build();

        syncLog = syncLogRepository.save(syncLog);
        return SyncLogResponse.fromEntity(syncLog);
    }

    @Transactional(readOnly = true)
    public List<SyncLogResponse> getSyncsByUser(Long userId) {
        return syncLogRepository.findByUserId(userId)
            .stream()
            .map(SyncLogResponse::fromEntity)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SyncLogResponse> getSyncsByStatus(Long userId, String status) {
        return syncLogRepository.findByUserIdAndStatus(userId, SyncLog.SyncStatus.valueOf(status))
            .stream()
            .map(SyncLogResponse::fromEntity)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SyncLogResponse getSyncById(Long id) {
        return syncLogRepository.findById(id)
            .map(SyncLogResponse::fromEntity)
            .orElseThrow(() -> new ResourceNotFoundException("Sync log not found"));
    }

    @Transactional(readOnly = true)
    public SyncLogResponse getLatestSync(Long userId) {
        SyncLog latest = syncLogRepository.findTopByUserIdOrderByCompletedAtDesc(userId);
        if (latest == null) {
            throw new ResourceNotFoundException("Sync log not found");
        }
        return SyncLogResponse.fromEntity(latest);
    }

    @Transactional
    public SyncLogResponse startSync(Long id) {
        SyncLog syncLog = syncLogRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Sync log not found"));

        syncLog.setSyncStatus(SyncLog.SyncStatus.IN_PROGRESS);
        syncLog.setStartedAt(LocalDateTime.now());
        syncLog = syncLogRepository.save(syncLog);
        return SyncLogResponse.fromEntity(syncLog);
    }

    @Transactional
    public SyncLogResponse completeSync(
            Long id,
            Integer itemsSynced,
            Integer conflictsDetected,
            Integer conflictsResolved,
            String resolutionStrategy,
            String syncNotes,
            String status) {
        SyncLog syncLog = syncLogRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Sync log not found"));

        syncLog.setItemsSynced(itemsSynced != null ? itemsSynced : syncLog.getItemsSynced());
        syncLog.setConflictsDetected(conflictsDetected != null ? conflictsDetected : syncLog.getConflictsDetected());
        syncLog.setConflictsResolved(conflictsResolved != null ? conflictsResolved : syncLog.getConflictsResolved());
        syncLog.setResolutionStrategy(resolutionStrategy != null ? resolutionStrategy : syncLog.getResolutionStrategy());
        syncLog.setSyncNotes(syncNotes != null ? syncNotes : syncLog.getSyncNotes());
        syncLog.setSyncStatus(status != null ? SyncLog.SyncStatus.valueOf(status) : SyncLog.SyncStatus.COMPLETED);
        syncLog.setCompletedAt(LocalDateTime.now());

        if (syncLog.getStartedAt() != null) {
            syncLog.setDurationSeconds(Duration.between(syncLog.getStartedAt(), syncLog.getCompletedAt()).getSeconds());
        }

        syncLog = syncLogRepository.save(syncLog);
        return SyncLogResponse.fromEntity(syncLog);
    }

    @Transactional
    public void deleteSync(Long id) {
        syncLogRepository.deleteById(id);
    }
}
