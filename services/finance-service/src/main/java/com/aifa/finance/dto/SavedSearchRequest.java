package com.aifa.finance.dto;

public record SavedSearchRequest(
    String searchName,
    String entityType,
    String filterCriteria,
    String sortField,
    String sortOrder,
    Boolean isDefault
) {}
