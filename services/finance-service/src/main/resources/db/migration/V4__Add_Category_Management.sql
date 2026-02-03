-- V4__Add_Category_Management.sql
-- Create categories and expense rules tables for smart expense categorization

-- Create categories table
CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(50),
    color VARCHAR(20),
    is_predefined BOOLEAN NOT NULL DEFAULT FALSE,
    monthly_budget DECIMAL(15, 2),
    spending_this_month DECIMAL(15, 2) DEFAULT 0.0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_categories_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for common queries
CREATE INDEX idx_category_user_name ON categories(user_id, name);
CREATE INDEX idx_category_user_ispredefined ON categories(user_id, is_predefined);
CREATE INDEX idx_category_is_active ON categories(is_active);

-- Create expense rules table
CREATE TABLE IF NOT EXISTS expense_rules (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    pattern VARCHAR(255) NOT NULL,
    rule_type VARCHAR(50) NOT NULL DEFAULT 'KEYWORD',
    match_type VARCHAR(50) NOT NULL DEFAULT 'CONTAINS',
    priority INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    match_count BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rules_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_rules_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- Create indexes for rule matching
CREATE INDEX idx_rule_user_category ON expense_rules(user_id, category_id);
CREATE INDEX idx_rule_user_pattern ON expense_rules(user_id, pattern);
CREATE INDEX idx_rule_is_active ON expense_rules(is_active);
CREATE INDEX idx_rule_priority ON expense_rules(priority DESC);

-- Add comment for documentation
COMMENT ON TABLE categories IS 'User expense categories (predefined and custom) with optional monthly budgets';
COMMENT ON TABLE expense_rules IS 'Rules for automatic expense categorization based on patterns';
COMMENT ON COLUMN expense_rules.rule_type IS 'Type of rule: KEYWORD, MERCHANT, or REGEX';
COMMENT ON COLUMN expense_rules.match_type IS 'How to match: EXACT, STARTS_WITH, or CONTAINS';
COMMENT ON COLUMN expense_rules.priority IS 'Priority order for rule matching (higher first)';
COMMENT ON COLUMN expense_rules.match_count IS 'Number of times this rule matched (for ML ranking)';
