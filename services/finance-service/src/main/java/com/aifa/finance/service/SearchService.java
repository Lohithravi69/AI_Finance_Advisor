package com.aifa.finance.service;

import com.aifa.finance.domain.SavedSearch;
import com.aifa.finance.domain.User;
import com.aifa.finance.dto.SavedSearchRequest;
import com.aifa.finance.dto.SavedSearchResponse;
import com.aifa.finance.exception.ResourceNotFoundException;
import com.aifa.finance.repository.SavedSearchRepository;
import com.aifa.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class SearchService {

    private final SavedSearchRepository savedSearchRepository;
    private final UserRepository userRepository;

    @Transactional
    public SavedSearchResponse saveSearch(Long userId, SavedSearchRequest request) {
        Long safeUserId = Objects.requireNonNull(userId, "userId must not be null");
        User user = userRepository.findById(safeUserId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        SavedSearch search = SavedSearch.builder()
            .user(user)
            .searchName(request.searchName())
            .entityType(SavedSearch.EntityType.valueOf(request.entityType()))
            .filterCriteria(request.filterCriteria())
            .sortField(request.sortField())
            .sortOrder(request.sortOrder() != null ? request.sortOrder() : "DESC")
            .isDefault(request.isDefault() != null ? request.isDefault() : false)
            .build();

        if (Boolean.TRUE.equals(request.isDefault())) {
            // Unset other defaults for this entity type
            SavedSearch oldDefault = savedSearchRepository.findDefaultSearchByUserAndType(
                safeUserId, SavedSearch.EntityType.valueOf(request.entityType()))
                .orElse(null);
            if (oldDefault != null) {
                oldDefault.setIsDefault(false);
                savedSearchRepository.save(oldDefault);
            }
        }

        search = Objects.requireNonNull(savedSearchRepository.save(search), "Saved search must not be null");
        return SavedSearchResponse.fromEntity(search);
    }

    @Transactional(readOnly = true)
    public List<SavedSearchResponse> getSearchesByUser(Long userId) {
        return savedSearchRepository.findByUserId(userId)
            .stream()
            .map(SavedSearchResponse::fromEntity)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SavedSearchResponse> getSearchesByEntityType(Long userId, String entityType) {
        return savedSearchRepository.findByUserIdAndEntityType(userId, 
            SavedSearch.EntityType.valueOf(entityType))
            .stream()
            .map(SavedSearchResponse::fromEntity)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SavedSearchResponse getSearchById(Long id) {
        return savedSearchRepository.findById(Objects.requireNonNull(id, "id must not be null"))
            .map(SavedSearchResponse::fromEntity)
            .orElseThrow(() -> new ResourceNotFoundException("Search not found"));
    }

    @Transactional
    public SavedSearchResponse updateSearch(Long id, SavedSearchRequest request) {
        Long safeId = Objects.requireNonNull(id, "id must not be null");
        SavedSearch search = savedSearchRepository.findById(safeId)
            .orElseThrow(() -> new ResourceNotFoundException("Search not found"));

        search.setSearchName(request.searchName());
        search.setFilterCriteria(request.filterCriteria());
        search.setSortField(request.sortField());
        search.setSortOrder(request.sortOrder() != null ? request.sortOrder() : "DESC");

        if (Boolean.TRUE.equals(request.isDefault())) {
            SavedSearch oldDefault = savedSearchRepository.findDefaultSearchByUserAndType(
                search.getUser().getId(), search.getEntityType())
                .orElse(null);
            if (oldDefault != null && !oldDefault.getId().equals(safeId)) {
                oldDefault.setIsDefault(false);
                savedSearchRepository.save(oldDefault);
            }
            search.setIsDefault(true);
        } else {
            search.setIsDefault(false);
        }

        search = Objects.requireNonNull(savedSearchRepository.save(search), "Updated search must not be null");
        return SavedSearchResponse.fromEntity(search);
    }

    @Transactional
    public void deleteSearch(Long id) {
        savedSearchRepository.deleteById(Objects.requireNonNull(id, "id must not be null"));
    }

    @Transactional
    public SavedSearchResponse executeSearch(Long id) {
        SavedSearch search = savedSearchRepository.findById(Objects.requireNonNull(id, "id must not be null"))
            .orElseThrow(() -> new ResourceNotFoundException("Search not found"));

        search.setLastUsedAt(LocalDateTime.now());
        savedSearchRepository.save(search);
        return SavedSearchResponse.fromEntity(search);
    }

    @Transactional(readOnly = true)
    public List<SavedSearchResponse> searchByName(Long userId, String name) {
        return savedSearchRepository.findByUserIdAndSearchNameContains(userId, name)
            .stream()
            .map(SavedSearchResponse::fromEntity)
            .collect(Collectors.toList());
    }
}
