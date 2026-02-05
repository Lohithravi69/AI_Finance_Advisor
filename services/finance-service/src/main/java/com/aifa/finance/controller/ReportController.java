package com.aifa.finance.controller;

import com.aifa.finance.dto.ReportRequest;
import com.aifa.finance.dto.ReportResponse;
import com.aifa.finance.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportResponse> generateReport(@RequestParam Long userId, @RequestBody ReportRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reportService.generateReport(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<ReportResponse>> getAllReports(@RequestParam Long userId) {
        return ResponseEntity.ok(reportService.getAllReports(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportResponse> getReportById(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getReportById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}
