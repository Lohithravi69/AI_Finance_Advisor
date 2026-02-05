-- V7__Add_Investments_Portfolio.sql
CREATE TABLE IF NOT EXISTS investments (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    account_id BIGINT,
    symbol VARCHAR(20) NOT NULL,
    name VARCHAR(255) NOT NULL,
    investment_type VARCHAR(50) NOT NULL,
    quantity NUMERIC(19, 8) NOT NULL,
    purchase_price NUMERIC(19, 8) NOT NULL,
    current_price NUMERIC(19, 8),
    total_cost NUMERIC(19, 2) NOT NULL,
    current_value NUMERIC(19, 2),
    gain_loss NUMERIC(19, 2),
    gain_loss_percentage NUMERIC(10, 4),
    purchase_date TIMESTAMP NOT NULL,
    last_updated TIMESTAMP,
    currency VARCHAR(3) DEFAULT 'USD',
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_investments_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_investments_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE SET NULL,
    CHECK (investment_type IN ('STOCK', 'BOND', 'MUTUAL_FUND', 'ETF', 'CRYPTOCURRENCY', 'COMMODITY', 'OPTION', 'REAL_ESTATE', 'OTHER'))
);

CREATE INDEX idx_investments_user_id ON investments(user_id);
CREATE INDEX idx_investments_account_id ON investments(account_id);
CREATE INDEX idx_investments_symbol ON investments(user_id, symbol);
CREATE INDEX idx_investments_type ON investments(user_id, investment_type);

CREATE TABLE IF NOT EXISTS portfolios (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    account_id BIGINT,
    portfolio_name VARCHAR(255) NOT NULL,
    description TEXT,
    total_invested NUMERIC(19, 2),
    current_value NUMERIC(19, 2),
    total_gain_loss NUMERIC(19, 2),
    total_return_percentage NUMERIC(10, 4),
    investment_count INTEGER DEFAULT 0,
    allocation_stocks NUMERIC(5, 2),
    allocation_bonds NUMERIC(5, 2),
    allocation_crypto NUMERIC(5, 2),
    allocation_other NUMERIC(5, 2),
    rebalance_needed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_portfolios_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_portfolios_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE SET NULL
);

CREATE INDEX idx_portfolios_user_id ON portfolios(user_id);
CREATE INDEX idx_portfolios_account_id ON portfolios(account_id);
