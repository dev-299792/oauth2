ALTER TABLE access_token
ADD COLUMN refresh_token_expires_at DATETIME DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE access_token
MODIFY COLUMN refresh_token_expires_at DATETIME NOT NULL;