-- V8__Add_Financial_Goals.sql
CREATE TABLE IF NOT EXISTS financial_goals (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    goal_name VARCHAR(255) NOT NULL,
    goal_category VARCHAR(50) NOT NULL,
    description TEXT,
    target_amount NUMERIC(19, 2) NOT NULL,
    current_amount NUMERIC(19, 2) DEFAULT 0,
    progress_percentage NUMERIC(5, 2) DEFAULT 0,
    start_date DATE NOT NULL,
    target_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    monthly_contribution NUMERIC(19, 2),
    account_id BIGINT,
    is_recurring BOOLEAN DEFAULT FALSE,
    completion_date DATE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_goals_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CHECK (goal_category IN ('SAVINGS', 'INVESTMENT', 'DEBT_PAYOFF', 'EMERGENCY_FUND', 'EDUCATION', 
                              'HOUSE', 'VACATION', 'CAR', 'RETIREMENT', 'WEDDING', 'BUSINESS', 'OTHER')),
    CHECK (status IN ('NOT_STARTED', 'IN_PROGRESS', 'PAUSED', 'COMPLETED', 'CANCELLED')),
    CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL'))
);

CREATE INDEX idx_goals_user_id ON financial_goals(user_id);
CREATE INDEX idx_goals_status ON financial_goals(user_id, status);
CREATE INDEX idx_goals_category ON financial_goals(user_id, goal_category);
CREATE INDEX idx_goals_target_date ON financial_goals(user_id, target_date);
