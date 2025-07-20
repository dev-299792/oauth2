CREATE TABLE access_token (
    token VARCHAR(255) PRIMARY KEY,
    refresh_token VARCHAR(255) UNIQUE NOT NULL,
    client_id VARCHAR(100) NOT NULL,
    username VARCHAR(100) NOT NULL,
    scopes VARCHAR(255),
    expires_at DATETIME NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_client_id_token FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE
);