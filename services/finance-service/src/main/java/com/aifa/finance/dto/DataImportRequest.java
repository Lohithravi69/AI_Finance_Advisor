package com.aifa.finance.dto;

public record DataImportRequest(
    String importType,
    String fileName,
    Integer totalRecords
) {}
