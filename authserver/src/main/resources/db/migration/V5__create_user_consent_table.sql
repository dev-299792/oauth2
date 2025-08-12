CREATE TABLE consents (
    id VARCHAR(100) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    client_id VARCHAR(255) NOT NULL,
    scope VARCHAR(1000) NOT NULL,
    granted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_consents_user FOREIGN KEY (user_id) REFERENCES users(id)

    UNIQUE KEY uniq_user_client_scope (user_id, client_id, scope)
);