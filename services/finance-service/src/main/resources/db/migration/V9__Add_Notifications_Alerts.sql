-- V9__Add_Notifications_Alerts.sql
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    is_sent BOOLEAN NOT NULL DEFAULT FALSE,
    priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    reference_id BIGINT,
    reference_type VARCHAR(100),
    action_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP,
    sent_at TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CHECK (notification_type IN ('BUDGET_ALERT', 'EXPENSE_ALERT', 'GOAL_MILESTONE', 'INCOME_RECEIVED', 'BILL_DUE', 'INVESTMENT_UPDATE', 'ACCOUNT_UPDATE', 'TRANSACTION_ALERT', 'SAVINGS_REMINDER', 'SYSTEM_MESSAGE')),
    CHECK (priority IN ('LOW', 'NORMAL', 'HIGH', 'CRITICAL'))
);

CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_read ON notifications(user_id, is_read);
CREATE INDEX idx_notifications_type ON notifications(user_id, notification_type);
CREATE INDEX idx_notifications_created_at ON notifications(user_id, created_at);

CREATE TABLE IF NOT EXISTS alert_rules (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    rule_name VARCHAR(255) NOT NULL,
    rule_type VARCHAR(50) NOT NULL,
    condition TEXT NOT NULL,
    threshold_value NUMERIC(19, 2),
    notification_type VARCHAR(50) NOT NULL,
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    frequency VARCHAR(50),
    last_triggered TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_alert_rules_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CHECK (rule_type IN ('BUDGET_EXCEEDED', 'EXPENSE_LIMIT', 'LOW_BALANCE', 'GOAL_PROGRESS', 'INCOME_RECEIVED', 'BILL_DUE', 'INVESTMENT_CHANGE', 'SAVINGS_TARGET', 'CUSTOM'))
);

CREATE INDEX idx_alert_rules_user_id ON alert_rules(user_id);
CREATE INDEX idx_alert_rules_enabled ON alert_rules(user_id, is_enabled);
