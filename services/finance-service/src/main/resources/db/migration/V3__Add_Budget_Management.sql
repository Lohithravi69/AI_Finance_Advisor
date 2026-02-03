-- V3__Add_Budget_Management.sql
CREATE TABLE budgets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    monthly_limit DECIMAL(12, 2) NOT NULL,
    spent_amount DECIMAL(12, 2) NOT NULL DEFAULT 0,
    start_date DATE NOT NULL,
    end_date DATE,
    alert_threshold INT DEFAULT 80,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_budget_user_id ON budgets(user_id);
CREATE INDEX idx_budget_user_period ON budgets(user_id, start_date, end_date);

CREATE TABLE budget_alerts (
    id BIGSERIAL PRIMARY KEY,
    budget_id BIGINT NOT NULL REFERENCES budgets(id) ON DELETE CASCADE,
    alert_type VARCHAR(50) NOT NULL,
    percentage INT NOT NULL,
    triggered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_budget_alert_budget_id ON budget_alerts(budget_id);
CREATE INDEX idx_budget_alert_triggered ON budget_alerts(triggered_at);
