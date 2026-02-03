package com.aifa.finance.service;

import com.aifa.finance.domain.Category;
import com.aifa.finance.domain.ExpenseRule;
import com.aifa.finance.domain.User;
import com.aifa.finance.domain.Transaction;
import com.aifa.finance.dto.*;
import com.aifa.finance.exception.ResourceNotFoundException;
import com.aifa.finance.repository.CategoryRepository;
import com.aifa.finance.repository.ExpenseRuleRepository;
import com.aifa.finance.repository.TransactionRepository;
import com.aifa.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing expense categories and categorization rules.
 * Handles auto-categorization, spending analysis, and anomaly detection.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final ExpenseRuleRepository expenseRuleRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    // Predefined default categories
    private static final List<String[]> DEFAULT_CATEGORIES = List.of(
        new String[]{"Groceries", "🛒", "#FF6B6B", "Food and grocery shopping"},
        new String[]{"Transportation", "🚗", "#4ECDC4", "Gas, public transport, rides"},
        new String[]{"Entertainment", "🎬", "#95E1D3", "Movies, games, hobbies"},
        new String[]{"Utilities", "💡", "#F7DC6F", "Electricity, water, internet"},
        new String[]{"Dining Out", "🍔", "#F8B739", "Restaurants and cafes"},
        new String[]{"Healthcare", "⚕️", "#BB8FCE", "Medical expenses and medicines"},
        new String[]{"Shopping", "🛍️", "#85C1E2", "Clothing and general shopping"},
        new String[]{"Subscriptions", "📱", "#A9DFBF", "Apps, streaming, memberships"},
        new String[]{"Savings", "🏦", "#F1948A", "Savings and investments"},
        new String[]{"Other", "📌", "#D5D8DC", "Miscellaneous expenses"}
    );

    /**
     * Initialize default categories for a new user
     */
    public void initializeDefaultCategories(User user) {
        log.info("Initializing default categories for user: {}", user.getId());
        
        for (String[] categoryData : DEFAULT_CATEGORIES) {
            Category category = Category.builder()
                .user(user)
                .name(categoryData[0])
                .icon(categoryData[1])
                .color(categoryData[2])
                .description(categoryData[3])
                .isPredefined(true)
                .isActive(true)
                .spendingThisMonth(0.0)
                .build();
            categoryRepository.save(category);
        }
    }

    /**
     * Create a new category for user
     */
    public CategoryResponse createCategory(Long userId, CategoryRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Category category = Category.builder()
            .user(user)
            .name(request.name())
            .description(request.description())
            .icon(request.icon())
            .color(request.color())
            .monthlyBudget(request.monthlyBudget())
            .isPredefined(false)
            .isActive(true)
            .spendingThisMonth(0.0)
            .build();
        
        Category saved = categoryRepository.save(category);
        log.info("Created category {} for user {}", saved.getName(), userId);
        
        return mapToResponse(saved);
    }

    /**
     * Get all categories for a user
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoriesByUser(Long userId) {
        return categoryRepository.findByUserIdOrderByNameAsc(userId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get only active categories for user
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getActiveCategoriesByUser(Long userId) {
        return categoryRepository.findActiveByUserId(userId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get a specific category
     */
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long userId, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        if (!category.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Category not found for this user");
        }
        
        return mapToResponse(category);
    }

    /**
     * Update a category
     */
    public CategoryResponse updateCategory(Long userId, Long categoryId, CategoryRequest request) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        if (!category.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Category not found for this user");
        }
        
        category.setName(request.name());
        category.setDescription(request.description());
        category.setIcon(request.icon());
        category.setColor(request.color());
        category.setMonthlyBudget(request.monthlyBudget());
        
        Category updated = categoryRepository.save(category);
        log.info("Updated category {} for user {}", categoryId, userId);
        
        return mapToResponse(updated);
    }

    /**
     * Delete a category
     */
    public void deleteCategory(Long userId, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        if (!category.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Category not found for this user");
        }
        
        // Prevent deletion of predefined categories
        if (category.getIsPredefined()) {
            throw new IllegalArgumentException("Cannot delete predefined categories");
        }
        
        // Delete associated rules
        expenseRuleRepository.deleteByCategoryId(categoryId);
        
        // Delete the category
        categoryRepository.deleteById(categoryId);
        log.info("Deleted category {} for user {}", categoryId, userId);
    }

    /**
     * Auto-categorize a transaction based on rules
     */
    public String autoCategorizeTransaction(Long userId, String description, String merchant) {
        List<ExpenseRule> rules = expenseRuleRepository.findActiveRulesByUserId(userId);
        
        for (ExpenseRule rule : rules) {
            String textToMatch = (description != null ? description : "") + " " + (merchant != null ? merchant : "");
            if (rule.matches(textToMatch)) {
                // Increment match count for ML ranking
                rule.setMatchCount(rule.getMatchCount() + 1);
                expenseRuleRepository.save(rule);
                
                log.debug("Auto-categorized to {} using rule {}", rule.getCategory().getName(), rule.getId());
                return rule.getCategory().getName();
            }
        }
        
        return "Other"; // Default fallback category
    }

    /**
     * Analyze spending by category with trend and anomaly detection
     */
    @Transactional(readOnly = true)
    public List<CategorySpendingAnalysisResponse> analyzeSpendingByCategory(Long userId) {
        List<Category> categories = categoryRepository.findActiveByUserId(userId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Transaction> transactions = transactionRepository.findByUserOrderByTransactionDateDesc(user).stream()
            .filter(t -> "EXPENSE".equalsIgnoreCase(t.getType()))
            .toList();
        
        // Calculate total spending for percentage calculation
        double totalSpending = transactions.stream()
            .mapToDouble(Transaction::getAmount)
            .sum();
        
        List<CategorySpendingAnalysisResponse> analysis = new ArrayList<>();
        
        for (Category category : categories) {
            // Filter transactions for this category
            List<Transaction> categoryTransactions = transactions.stream()
                .filter(t -> category.getName().equalsIgnoreCase(t.getCategory()))
                .collect(Collectors.toList());
            
            double categorySpent = categoryTransactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
            
            double percentageOfTotal = totalSpending > 0 ? (categorySpent / totalSpending) * 100 : 0;
            double percentageOfBudget = category.getMonthlyBudget() != null && category.getMonthlyBudget() > 0
                ? (categorySpent / category.getMonthlyBudget()) * 100 : 0;
            
            // Detect anomalies (spending > 2x average or > 150% of budget)
            double averageTransaction = !categoryTransactions.isEmpty() 
                ? categorySpent / categoryTransactions.size() : 0;
            boolean isAnomalous = categorySpent > (averageTransaction * 2) || percentageOfBudget > 150;
            
            // Determine trend (simplified: compare this month to average of previous months)
            String trend = determineTrend(categoryTransactions);
            
            analysis.add(new CategorySpendingAnalysisResponse(
                category.getId(),
                category.getName(),
                category.getIcon(),
                category.getColor(),
                categorySpent,
                category.getMonthlyBudget(),
                percentageOfTotal,
                percentageOfBudget,
                categoryTransactions.size(),
                averageTransaction,
                trend,
                isAnomalous
            ));
        }
        
        return analysis.stream()
            .sorted((a, b) -> Double.compare(b.totalSpent(), a.totalSpent()))
            .collect(Collectors.toList());
    }

    /**
     * Determine spending trend for a category
     */
    private String determineTrend(List<Transaction> transactions) {
        if (transactions.size() < 2) return "STABLE";
        
        LocalDate now = LocalDate.now();
        LocalDate lastMonth = now.minusMonths(1);
        
        double thisMonthSpent = transactions.stream()
            .filter(t -> t.getTransactionDate().getMonth() == now.getMonth())
            .mapToDouble(Transaction::getAmount)
            .sum();
        
        double lastMonthSpent = transactions.stream()
            .filter(t -> t.getTransactionDate().getMonth() == lastMonth.getMonth())
            .mapToDouble(Transaction::getAmount)
            .sum();
        
        if (lastMonthSpent == 0) return "STABLE";
        
        double percentageChange = ((thisMonthSpent - lastMonthSpent) / lastMonthSpent) * 100;
        
        if (percentageChange > 10) return "UP";
        if (percentageChange < -10) return "DOWN";
        return "STABLE";
    }

    /**
     * Get spending breakdown by category (for pie charts, etc.)
     */
    @Transactional(readOnly = true)
    public Map<String, Double> getSpendingBreakdown(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Transaction> transactions = transactionRepository.findByUserOrderByTransactionDateDesc(user).stream()
            .filter(t -> "EXPENSE".equalsIgnoreCase(t.getType()))
            .toList();
        
        return transactions.stream()
            .collect(Collectors.groupingBy(
                Transaction::getCategory,
                Collectors.summingDouble(Transaction::getAmount)
            ));
    }

    /**
     * Get categories over budget
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getOverBudgetCategories(Long userId) {
        return categoryRepository.findOverBudgetCategories(userId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Create or suggest an expense rule
     */
    public ExpenseRuleResponse createRule(Long userId, ExpenseRuleRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        if (!category.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Category not found for this user");
        }
        
        ExpenseRule rule = ExpenseRule.builder()
            .user(user)
            .category(category)
            .pattern(request.pattern())
            .ruleType(ExpenseRule.RuleType.valueOf(request.ruleType() != null ? request.ruleType() : "KEYWORD"))
            .matchType(request.matchType() != null ? request.matchType() : "CONTAINS")
            .priority(request.priority() != null ? request.priority() : 0)
            .isActive(true)
            .matchCount(0L)
            .build();
        
        ExpenseRule saved = expenseRuleRepository.save(rule);
        log.info("Created rule {} for category {} (user {})", rule.getId(), category.getId(), userId);
        
        return mapRuleToResponse(saved);
    }

    /**
     * Get all rules for a user
     */
    @Transactional(readOnly = true)
    public List<ExpenseRuleResponse> getRulesByUser(Long userId) {
        return expenseRuleRepository.findActiveRulesByUserId(userId)
            .stream()
            .map(this::mapRuleToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Delete a rule
     */
    public void deleteRule(Long userId, Long ruleId) {
        ExpenseRule rule = expenseRuleRepository.findById(ruleId)
            .orElseThrow(() -> new ResourceNotFoundException("Rule not found"));
        
        if (!rule.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Rule not found for this user");
        }
        
        expenseRuleRepository.deleteById(ruleId);
        log.info("Deleted rule {} for user {}", ruleId, userId);
    }

    /**
     * Map Category entity to response DTO
     */
    private CategoryResponse mapToResponse(Category category) {
        return new CategoryResponse(
            category.getId(),
            category.getName(),
            category.getDescription(),
            category.getIcon(),
            category.getColor(),
            category.getIsPredefined(),
            category.getMonthlyBudget(),
            category.getSpendingThisMonth(),
            category.getPercentageSpent(),
            category.isOverBudget(),
            category.getIsActive(),
            category.getCreatedAt(),
            category.getUpdatedAt()
        );
    }

    /**
     * Map ExpenseRule entity to response DTO
     */
    private ExpenseRuleResponse mapRuleToResponse(ExpenseRule rule) {
        return new ExpenseRuleResponse(
            rule.getId(),
            rule.getCategory().getId(),
            rule.getCategory().getName(),
            rule.getPattern(),
            rule.getRuleType().toString(),
            rule.getMatchType(),
            rule.getPriority(),
            rule.getIsActive(),
            rule.getMatchCount(),
            rule.getCreatedAt()
        );
    }
}
