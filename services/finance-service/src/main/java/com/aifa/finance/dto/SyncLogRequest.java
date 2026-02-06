package com.aifa.finance.dto;

public record SyncLogRequest(
    String syncType,
    String sourceSystem,
    String resolutionStrategy,
    String syncNotes
) {}
