package com.aifa.finance.dto;

import com.aifa.finance.domain.SavedSearch;

import java.time.LocalDateTime;

public record SavedSearchResponse(
    Long id,
    String searchName,
    String entityType,
    String filterCriteria,
    String sortField,
    String sortOrder,
    Boolean isDefault,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime lastUsedAt
) {
    public static SavedSearchResponse fromEntity(SavedSearch search) {
        return new SavedSearchResponse(
            search.getId(),
            search.getSearchName(),
            search.getEntityType().toString(),
            search.getFilterCriteria(),
            search.getSortField(),
            search.getSortOrder(),
            search.getIsDefault(),
            search.getCreatedAt(),
            search.getUpdatedAt(),
            search.getLastUsedAt()
        );
    }
}
