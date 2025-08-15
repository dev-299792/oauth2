-- all existing clients defaulted to confidential
ALTER TABLE client
ADD COLUMN is_public BOOLEAN DEFAULT FALSE;

-- enforce all new clients to be public by default
ALTER TABLE client
MODIFY COLUMN is_public BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE authorization_code
ADD COLUMN code_challenge VARCHAR(255);

ALTER TABLE authorization_code
ADD COLUMN code_challenge_method VARCHAR(15);