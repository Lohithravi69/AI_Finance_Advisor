package com.aifa.finance.controller;

import com.aifa.finance.dto.DataImportRequest;
import com.aifa.finance.dto.DataImportResponse;
import com.aifa.finance.service.DataImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/imports")
@RequiredArgsConstructor
public class DataImportController {

    private final DataImportService dataImportService;

    @PostMapping
    public ResponseEntity<DataImportResponse> createImport(
            @RequestParam Long userId,
            @RequestBody DataImportRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(dataImportService.createImport(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<DataImportResponse>> getImportsByUser(@RequestParam Long userId) {
        return ResponseEntity.ok(dataImportService.getImportsByUser(userId));
    }

    @GetMapping("/status")
    public ResponseEntity<List<DataImportResponse>> getImportsByStatus(
            @RequestParam Long userId,
            @RequestParam String status) {
        return ResponseEntity.ok(dataImportService.getImportsByStatus(userId, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataImportResponse> getImportById(@PathVariable Long id) {
        return ResponseEntity.ok(dataImportService.getImportById(id));
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<DataImportResponse> startImport(@PathVariable Long id) {
        return ResponseEntity.ok(dataImportService.startImport(id));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<DataImportResponse> completeImport(
            @PathVariable Long id,
            @RequestParam(required = false) Integer processedRecords,
            @RequestParam(required = false) Integer successCount,
            @RequestParam(required = false) Integer errorCount,
            @RequestParam(required = false) String errorDetails) {
        return ResponseEntity.ok(dataImportService.completeImport(
            id, processedRecords, successCount, errorCount, errorDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImport(@PathVariable Long id) {
        dataImportService.deleteImport(id);
        return ResponseEntity.noContent().build();
    }
}
