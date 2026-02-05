-- V5__Add_Accounts_Income.sql
CREATE TABLE IF NOT EXISTS accounts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    account_name VARCHAR(255) NOT NULL,
    account_type VARCHAR(50) NOT NULL,
    institution_name VARCHAR(255),
    account_number VARCHAR(255),
    current_balance NUMERIC(19, 2) NOT NULL DEFAULT 0,
    available_balance NUMERIC(19, 2),
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    account_color VARCHAR(7),
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_accounts_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CHECK (account_type IN ('CHECKING', 'SAVINGS', 'CREDIT_CARD', 'INVESTMENT', 'LOAN', 'OTHER'))
);

CREATE INDEX idx_accounts_user_id ON accounts(user_id);
CREATE INDEX idx_accounts_user_type ON accounts(user_id, account_type);
CREATE INDEX idx_accounts_is_active ON accounts(user_id, is_active);
CREATE INDEX idx_accounts_is_primary ON accounts(user_id, is_primary);

CREATE TABLE IF NOT EXISTS income_sources (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    source_name VARCHAR(255) NOT NULL,
    description TEXT,
    amount NUMERIC(19, 2) NOT NULL,
    frequency VARCHAR(50) NOT NULL,
    income_type VARCHAR(100),
    start_date DATE NOT NULL,
    end_date DATE,
    last_received DATE,
    next_expected DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_income_sources_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_income_sources_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
    CHECK (frequency IN ('WEEKLY', 'BIWEEKLY', 'MONTHLY', 'QUARTERLY', 'ANNUAL'))
);

CREATE INDEX idx_income_sources_user_id ON income_sources(user_id);
CREATE INDEX idx_income_sources_account_id ON income_sources(account_id);
CREATE INDEX idx_income_sources_user_account ON income_sources(user_id, account_id);
CREATE INDEX idx_income_sources_start_date ON income_sources(user_id, start_date);
CREATE INDEX idx_income_sources_end_date ON income_sources(user_id, end_date);
