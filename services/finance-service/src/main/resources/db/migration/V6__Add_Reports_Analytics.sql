-- V6__Add_Reports_Analytics.sql
CREATE TABLE IF NOT EXISTS reports (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    report_type VARCHAR(50) NOT NULL,
    report_name VARCHAR(255) NOT NULL,
    description TEXT,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    report_data TEXT,
    file_path VARCHAR(500),
    file_format VARCHAR(10),
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_scheduled BOOLEAN NOT NULL DEFAULT FALSE,
    schedule_frequency VARCHAR(50),
    next_generation TIMESTAMP,
    CONSTRAINT fk_reports_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CHECK (report_type IN ('EXPENSE_SUMMARY', 'INCOME_SUMMARY', 'BUDGET_ANALYSIS', 'CATEGORY_BREAKDOWN', 
                           'MONTHLY_COMPARISON', 'YEARLY_COMPARISON', 'NET_WORTH_TREND', 'CASH_FLOW', 
                           'SAVINGS_RATE', 'INVESTMENT_PERFORMANCE', 'CUSTOM'))
);

CREATE INDEX idx_reports_user_id ON reports(user_id);
CREATE INDEX idx_reports_report_type ON reports(user_id, report_type);
CREATE INDEX idx_reports_generated_at ON reports(user_id, generated_at);

CREATE TABLE IF NOT EXISTS analytics_snapshots (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    snapshot_date DATE NOT NULL,
    total_expenses NUMERIC(19, 2),
    total_income NUMERIC(19, 2),
    net_savings NUMERIC(19, 2),
    savings_rate NUMERIC(5, 2),
    total_assets NUMERIC(19, 2),
    total_liabilities NUMERIC(19, 2),
    net_worth NUMERIC(19, 2),
    liquid_assets NUMERIC(19, 2),
    budget_utilization NUMERIC(5, 2),
    over_budget_count INTEGER,
    top_category VARCHAR(255),
    top_category_amount NUMERIC(19, 2),
    transaction_count INTEGER,
    average_transaction NUMERIC(19, 2),
    active_goals_count INTEGER,
    completed_goals_count INTEGER,
    total_goal_progress NUMERIC(5, 2),
    CONSTRAINT fk_analytics_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE (user_id, snapshot_date)
);

CREATE INDEX idx_analytics_user_id ON analytics_snapshots(user_id);
CREATE INDEX idx_analytics_snapshot_date ON analytics_snapshots(user_id, snapshot_date);
