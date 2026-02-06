package com.aifa.finance.controller;

import com.aifa.finance.dto.SavedSearchRequest;
import com.aifa.finance.dto.SavedSearchResponse;
import com.aifa.finance.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/searches")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @PostMapping
    public ResponseEntity<SavedSearchResponse> saveSearch(
            @RequestParam Long userId,
            @RequestBody SavedSearchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(searchService.saveSearch(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<SavedSearchResponse>> getSearchesByUser(
            @RequestParam Long userId) {
        return ResponseEntity.ok(searchService.getSearchesByUser(userId));
    }

    @GetMapping("/by-type")
    public ResponseEntity<List<SavedSearchResponse>> getSearchesByType(
            @RequestParam Long userId,
            @RequestParam String entityType) {
        return ResponseEntity.ok(searchService.getSearchesByEntityType(userId, entityType));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SavedSearchResponse> getSearchById(
            @PathVariable Long id) {
        return ResponseEntity.ok(searchService.getSearchById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavedSearchResponse> updateSearch(
            @PathVariable Long id,
            @RequestBody SavedSearchRequest request) {
        return ResponseEntity.ok(searchService.updateSearch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSearch(@PathVariable Long id) {
        searchService.deleteSearch(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/execute")
    public ResponseEntity<SavedSearchResponse> executeSearch(@PathVariable Long id) {
        return ResponseEntity.ok(searchService.executeSearch(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<SavedSearchResponse>> searchByName(
            @RequestParam Long userId,
            @RequestParam String name) {
        return ResponseEntity.ok(searchService.searchByName(userId, name));
    }
}
