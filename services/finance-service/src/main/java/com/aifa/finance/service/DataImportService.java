package com.aifa.finance.service;

import com.aifa.finance.domain.DataImport;
import com.aifa.finance.domain.User;
import com.aifa.finance.dto.DataImportRequest;
import com.aifa.finance.dto.DataImportResponse;
import com.aifa.finance.exception.ResourceNotFoundException;
import com.aifa.finance.repository.DataImportRepository;
import com.aifa.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DataImportService {

    private final DataImportRepository dataImportRepository;
    private final UserRepository userRepository;

    @Transactional
    public DataImportResponse createImport(Long userId, DataImportRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        DataImport dataImport = DataImport.builder()
            .user(user)
            .importType(DataImport.ImportType.valueOf(request.importType()))
            .fileName(request.fileName())
            .importStatus(DataImport.ImportStatus.PENDING)
            .totalRecords(request.totalRecords() != null ? request.totalRecords() : 0)
            .processedRecords(0)
            .successCount(0)
            .errorCount(0)
            .build();

        dataImport = dataImportRepository.save(dataImport);
        return DataImportResponse.fromEntity(dataImport);
    }

    @Transactional(readOnly = true)
    public List<DataImportResponse> getImportsByUser(Long userId) {
        return dataImportRepository.findByUserId(userId)
            .stream()
            .map(DataImportResponse::fromEntity)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DataImportResponse> getImportsByStatus(Long userId, String status) {
        return dataImportRepository.findByUserIdAndStatus(userId,
            DataImport.ImportStatus.valueOf(status))
            .stream()
            .map(DataImportResponse::fromEntity)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DataImportResponse getImportById(Long id) {
        return dataImportRepository.findById(id)
            .map(DataImportResponse::fromEntity)
            .orElseThrow(() -> new ResourceNotFoundException("Import not found"));
    }

    @Transactional
    public DataImportResponse startImport(Long id) {
        DataImport dataImport = dataImportRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Import not found"));

        dataImport.setImportStatus(DataImport.ImportStatus.IN_PROGRESS);
        dataImport.setStartedAt(LocalDateTime.now());
        dataImport = dataImportRepository.save(dataImport);
        return DataImportResponse.fromEntity(dataImport);
    }

    @Transactional
    public DataImportResponse completeImport(
            Long id,
            Integer processedRecords,
            Integer successCount,
            Integer errorCount,
            String errorDetails) {
        DataImport dataImport = dataImportRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Import not found"));

        dataImport.setProcessedRecords(processedRecords != null ? processedRecords : dataImport.getProcessedRecords());
        dataImport.setSuccessCount(successCount != null ? successCount : dataImport.getSuccessCount());
        dataImport.setErrorCount(errorCount != null ? errorCount : dataImport.getErrorCount());
        dataImport.setErrorDetails(errorDetails);
        dataImport.setCompletedAt(LocalDateTime.now());

        if (dataImport.getErrorCount() != null && dataImport.getErrorCount() > 0) {
            if (dataImport.getSuccessCount() != null && dataImport.getSuccessCount() > 0) {
                dataImport.setImportStatus(DataImport.ImportStatus.PARTIALLY_COMPLETED);
            } else {
                dataImport.setImportStatus(DataImport.ImportStatus.FAILED);
            }
        } else {
            dataImport.setImportStatus(DataImport.ImportStatus.COMPLETED);
        }

        dataImport = dataImportRepository.save(dataImport);
        return DataImportResponse.fromEntity(dataImport);
    }

    @Transactional
    public void deleteImport(Long id) {
        dataImportRepository.deleteById(id);
    }
}
