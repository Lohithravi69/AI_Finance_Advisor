package com.aifa.finance.service;

import com.aifa.finance.domain.Report;
import com.aifa.finance.domain.User;
import com.aifa.finance.dto.ReportRequest;
import com.aifa.finance.dto.ReportResponse;
import com.aifa.finance.exception.ResourceNotFoundException;
import com.aifa.finance.repository.ReportRepository;
import com.aifa.finance.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public ReportResponse generateReport(Long userId, ReportRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Report.ReportType reportType = Report.ReportType.valueOf(request.reportType());

        // Placeholder report data
        String reportData = "{}";

        LocalDateTime nextGen = null;
        if (Boolean.TRUE.equals(request.isScheduled()) && request.scheduleFrequency() != null) {
            nextGen = calculateNextGeneration(request.scheduleFrequency());
        }

        Report report = Report.builder()
            .user(user)
            .reportType(reportType)
            .reportName(request.reportName())
            .description(request.description())
            .startDate(request.startDate())
            .endDate(request.endDate())
            .reportData(reportData)
            .fileFormat(request.fileFormat())
            .isScheduled(request.isScheduled() != null ? request.isScheduled() : false)
            .scheduleFrequency(request.scheduleFrequency())
            .nextGeneration(nextGen)
            .build();

        report = reportRepository.save(report);
        return toResponse(report);
    }

    private LocalDateTime calculateNextGeneration(String frequency) {
        return switch (frequency) {
            case "DAILY" -> LocalDateTime.now().plusDays(1);
            case "WEEKLY" -> LocalDateTime.now().plusWeeks(1);
            case "MONTHLY" -> LocalDateTime.now().plusMonths(1);
            default -> null;
        };
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> getAllReports(Long userId) {
        return reportRepository.findByUserIdOrderByGeneratedAtDesc(userId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReportResponse getReportById(Long reportId) {
        return reportRepository.findById(reportId)
            .map(this::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
    }

    @Transactional
    public void deleteReport(Long reportId) {
        reportRepository.deleteById(reportId);
    }

    private ReportResponse toResponse(Report report) {
        return new ReportResponse(
            report.getId(),
            report.getReportName(),
            report.getReportType().name(),
            report.getDescription(),
            report.getStartDate(),
            report.getEndDate(),
            report.getReportData(),
            report.getFilePath(),
            report.getFileFormat(),
            report.getGeneratedAt(),
            report.getIsScheduled(),
            report.getScheduleFrequency(),
            report.getNextGeneration()
        );
    }
}
