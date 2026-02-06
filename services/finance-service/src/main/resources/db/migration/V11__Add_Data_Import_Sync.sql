CREATE TABLE IF NOT EXISTS data_imports (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    import_type VARCHAR(50) NOT NULL,
    file_name VARCHAR(255),
    import_status VARCHAR(50) NOT NULL,
    total_records INTEGER DEFAULT 0,
    processed_records INTEGER DEFAULT 0,
    success_count INTEGER DEFAULT 0,
    error_count INTEGER DEFAULT 0,
    error_details TEXT,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CHECK (import_type IN ('CSV', 'BANK_API', 'QIF', 'OFX', 'JSON', 'SPREADSHEET')),
    CHECK (import_status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'FAILED', 'PARTIALLY_COMPLETED'))
);

CREATE TABLE IF NOT EXISTS sync_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    sync_type VARCHAR(50) NOT NULL,
    sync_status VARCHAR(50) NOT NULL,
    source_system VARCHAR(100) NOT NULL,
    items_synced INTEGER DEFAULT 0,
    conflicts_detected INTEGER DEFAULT 0,
    conflicts_resolved INTEGER DEFAULT 0,
    resolution_strategy VARCHAR(50),
    sync_notes TEXT,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    duration_seconds BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CHECK (sync_type IN ('CLOUD_UPLOAD', 'CLOUD_DOWNLOAD', 'BIDIRECTIONAL')),
    CHECK (sync_status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'FAILED', 'PARTIALLY_COMPLETED'))
);

CREATE INDEX idx_data_imports_user_id ON data_imports(user_id);
CREATE INDEX idx_data_imports_status ON data_imports(user_id, import_status);
CREATE INDEX idx_data_imports_type ON data_imports(user_id, import_type);

CREATE INDEX idx_sync_logs_user_id ON sync_logs(user_id);
CREATE INDEX idx_sync_logs_status ON sync_logs(user_id, sync_status);
CREATE INDEX idx_sync_logs_type ON sync_logs(user_id, sync_type);
