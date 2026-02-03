package com.aifa.finance.repository;

import com.aifa.finance.domain.ExpenseRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRuleRepository extends JpaRepository<ExpenseRule, Long> {
    
    /**
     * Find all active rules for a user, ordered by priority (highest first)
     */
    @Query("SELECT r FROM ExpenseRule r WHERE r.user.id = :userId AND r.isActive = true ORDER BY r.priority DESC, r.matchCount DESC")
    List<ExpenseRule> findActiveRulesByUserId(@Param("userId") Long userId);

    /**
     * Find rules for a specific category
     */
    List<ExpenseRule> findByUserIdAndCategoryIdAndIsActive(Long userId, Long categoryId, Boolean isActive);

    /**
     * Find rules by pattern (for searching/updating)
     */
    @Query("SELECT r FROM ExpenseRule r WHERE r.user.id = :userId AND LOWER(r.pattern) = LOWER(:pattern)")
    List<ExpenseRule> findByUserIdAndPattern(@Param("userId") Long userId, @Param("pattern") String pattern);

    /**
     * Find rules matching a specific text pattern
     */
    @Query("SELECT r FROM ExpenseRule r WHERE r.user.id = :userId AND r.isActive = true AND " +
           "(r.ruleType = 'KEYWORD' OR r.ruleType = 'MERCHANT') ORDER BY r.priority DESC")
    List<ExpenseRule> findMatchingRules(@Param("userId") Long userId);

    /**
     * Count rules for a user
     */
    Long countByUserId(Long userId);

    /**
     * Count active rules for a user
     */
    @Query("SELECT COUNT(r) FROM ExpenseRule r WHERE r.user.id = :userId AND r.isActive = true")
    Long countActiveRules(@Param("userId") Long userId);

    /**
     * Delete all rules for a user
     */
    void deleteByUserId(Long userId);

    /**
     * Delete rules for a specific category
     */
    void deleteByCategoryId(Long categoryId);
}
