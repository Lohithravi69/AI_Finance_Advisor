CREATE TABLE IF NOT EXISTS saved_searches (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    search_name VARCHAR(255) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    filter_criteria TEXT NOT NULL,
    sort_field VARCHAR(100),
    sort_order VARCHAR(10),
    is_default BOOLEAN DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_used_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CHECK (entity_type IN ('TRANSACTION', 'ACCOUNT', 'INVESTMENT', 'GOAL', 'REPORT', 'NOTIFICATION')),
    CHECK (sort_order IS NULL OR sort_order IN ('ASC', 'DESC'))
);

CREATE INDEX idx_saved_searches_user_id ON saved_searches(user_id);
CREATE INDEX idx_saved_searches_entity_type ON saved_searches(user_id, entity_type);
CREATE INDEX idx_saved_searches_default ON saved_searches(user_id, is_default);
CREATE INDEX idx_saved_searches_name ON saved_searches(user_id, search_name);
