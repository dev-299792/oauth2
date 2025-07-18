CREATE TABLE client (
  id VARCHAR(100) PRIMARY KEY,
  client_id VARCHAR(100) NOT NULL,
  client_id_issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  client_secret VARCHAR(200),
  client_secret_expires_at TIMESTAMP NULL,
  client_name VARCHAR(200) NOT NULL,
  client_authentication_methods VARCHAR(1000) NOT NULL,
  authorization_grant_types VARCHAR(1000) NOT NULL,
  redirect_uris VARCHAR(1000),
  scopes VARCHAR(1000),
  created_by VARCHAR(50),
  UNIQUE (client_id),
  CONSTRAINT fk_client_user FOREIGN KEY (created_by) REFERENCES users(username)
);