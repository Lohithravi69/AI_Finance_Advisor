package com.aifa.finance.controller;

import com.aifa.finance.dto.SyncLogRequest;
import com.aifa.finance.dto.SyncLogResponse;
import com.aifa.finance.service.SyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/syncs")
@RequiredArgsConstructor
public class SyncController {

    private final SyncService syncService;

    @PostMapping
    public ResponseEntity<SyncLogResponse> createSync(
            @RequestParam Long userId,
            @RequestBody SyncLogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(syncService.createSync(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<SyncLogResponse>> getSyncsByUser(@RequestParam Long userId) {
        return ResponseEntity.ok(syncService.getSyncsByUser(userId));
    }

    @GetMapping("/status")
    public ResponseEntity<List<SyncLogResponse>> getSyncsByStatus(
            @RequestParam Long userId,
            @RequestParam String status) {
        return ResponseEntity.ok(syncService.getSyncsByStatus(userId, status));
    }

    @GetMapping("/latest")
    public ResponseEntity<SyncLogResponse> getLatestSync(@RequestParam Long userId) {
        return ResponseEntity.ok(syncService.getLatestSync(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SyncLogResponse> getSyncById(@PathVariable Long id) {
        return ResponseEntity.ok(syncService.getSyncById(id));
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<SyncLogResponse> startSync(@PathVariable Long id) {
        return ResponseEntity.ok(syncService.startSync(id));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<SyncLogResponse> completeSync(
            @PathVariable Long id,
            @RequestParam(required = false) Integer itemsSynced,
            @RequestParam(required = false) Integer conflictsDetected,
            @RequestParam(required = false) Integer conflictsResolved,
            @RequestParam(required = false) String resolutionStrategy,
            @RequestParam(required = false) String syncNotes,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(syncService.completeSync(
            id, itemsSynced, conflictsDetected, conflictsResolved, resolutionStrategy, syncNotes, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSync(@PathVariable Long id) {
        syncService.deleteSync(id);
        return ResponseEntity.noContent().build();
    }
}
