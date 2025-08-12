CREATE TABLE users (
    id VARCHAR(100) PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    enabled BOOLEAN DEFAULT TRUE
);

CREATE TABLE authorities (
  user_id VARCHAR(50) NOT NULL,
  authority VARCHAR(50) NOT NULL,
  CONSTRAINT fk_authorities_users FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE UNIQUE INDEX ix_auth_user_id ON authorities (user_id, authority);