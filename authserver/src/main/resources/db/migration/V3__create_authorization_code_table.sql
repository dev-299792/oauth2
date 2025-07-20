CREATE TABLE authorization_code (
    code VARCHAR(255) PRIMARY KEY,
    client_id VARCHAR(100) NOT NULL,
    username VARCHAR(100) NOT NULL,
    redirect_uri VARCHAR(255),
    scopes VARCHAR(255),
    expires_at DATETIME NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_client_id FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE
);