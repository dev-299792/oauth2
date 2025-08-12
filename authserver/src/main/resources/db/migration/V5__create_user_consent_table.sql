CREATE TABLE consents (
    id VARCHAR(100) PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    client_id VARCHAR(100) NOT NULL,
    scope VARCHAR(1000) NOT NULL,
    granted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_consents_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_consents_client FOREIGN KEY (client_id) REFERENCES client(client_id),
    UNIQUE KEY uniq_user_client (user_id, client_id)
);