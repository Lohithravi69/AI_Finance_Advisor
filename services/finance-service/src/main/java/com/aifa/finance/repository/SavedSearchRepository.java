package com.aifa.finance.repository;

import com.aifa.finance.domain.SavedSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedSearchRepository extends JpaRepository<SavedSearch, Long> {

    @Query("SELECT s FROM SavedSearch s WHERE s.user.id = :userId ORDER BY s.createdAt DESC")
    List<SavedSearch> findByUserId(Long userId);

    @Query("SELECT s FROM SavedSearch s WHERE s.user.id = :userId AND s.entityType = :entityType " +
           "ORDER BY s.createdAt DESC")
    List<SavedSearch> findByUserIdAndEntityType(Long userId, SavedSearch.EntityType entityType);

    @Query("SELECT s FROM SavedSearch s WHERE s.user.id = :userId AND s.isDefault = true " +
           "AND s.entityType = :entityType")
    Optional<SavedSearch> findDefaultSearchByUserAndType(Long userId, SavedSearch.EntityType entityType);

    @Query("SELECT s FROM SavedSearch s WHERE s.user.id = :userId AND s.searchName LIKE CONCAT('%', :name, '%') " +
           "ORDER BY s.createdAt DESC")
    List<SavedSearch> findByUserIdAndSearchNameContains(Long userId, String name);

    @Query("SELECT COUNT(s) FROM SavedSearch s WHERE s.user.id = :userId AND s.entityType = :entityType")
    long countByUserIdAndEntityType(Long userId, SavedSearch.EntityType entityType);
}
