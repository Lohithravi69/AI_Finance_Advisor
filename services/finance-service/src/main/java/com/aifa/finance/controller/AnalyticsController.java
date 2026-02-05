package com.aifa.finance.controller;

import com.aifa.finance.dto.AnalyticsSnapshotResponse;
import com.aifa.finance.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @PostMapping("/snapshot")
    public ResponseEntity<AnalyticsSnapshotResponse> generateDailySnapshot(@RequestParam Long userId) {
        return ResponseEntity.ok(analyticsService.generateDailySnapshot(userId));
    }

    @GetMapping("/snapshot/latest")
    public ResponseEntity<AnalyticsSnapshotResponse> getLatestSnapshot(@RequestParam Long userId) {
        return ResponseEntity.ok(analyticsService.getLatestSnapshot(userId));
    }

    @GetMapping("/snapshot/history")
    public ResponseEntity<List<AnalyticsSnapshotResponse>> getSnapshotHistory(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(analyticsService.getSnapshotHistory(userId, startDate, endDate));
    }

    @GetMapping("/snapshot/last30days")
    public ResponseEntity<List<AnalyticsSnapshotResponse>> getLast30Days(@RequestParam Long userId) {
        return ResponseEntity.ok(analyticsService.getLast30Days(userId));
    }
}
