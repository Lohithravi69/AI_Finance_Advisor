package com.aifa.finance.repository;

import com.aifa.finance.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * Find all categories for a specific user
     */
    List<Category> findByUserIdOrderByNameAsc(Long userId);

    /**
     * Find all active categories for a user
     */
    @Query("SELECT c FROM Category c WHERE c.user.id = :userId AND c.isActive = true ORDER BY c.name ASC")
    List<Category> findActiveByUserId(@Param("userId") Long userId);

    /**
     * Find predefined (system) categories
     */
    @Query("SELECT c FROM Category c WHERE c.isPredefined = true ORDER BY c.name ASC")
    List<Category> findPredefinedCategories();

    /**
     * Find a category by name for a user
     */
    Optional<Category> findByUserIdAndNameIgnoreCase(Long userId, String name);

    /**
     * Find categories with budgets for a user
     */
    @Query("SELECT c FROM Category c WHERE c.user.id = :userId AND c.monthlyBudget IS NOT NULL AND c.monthlyBudget > 0 ORDER BY c.name ASC")
    List<Category> findCategoriesWithBudget(@Param("userId") Long userId);

    /**
     * Find categories over budget
     */
    @Query("SELECT c FROM Category c WHERE c.user.id = :userId AND c.monthlyBudget > 0 AND c.spendingThisMonth > c.monthlyBudget")
    List<Category> findOverBudgetCategories(@Param("userId") Long userId);

    /**
     * Count categories for a user
     */
    Long countByUserId(Long userId);

    /**
     * Delete all categories for a user
     */
    void deleteByUserId(Long userId);
}
