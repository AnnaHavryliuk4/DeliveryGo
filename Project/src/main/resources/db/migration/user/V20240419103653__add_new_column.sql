ALTER TABLE users
    ADD COLUMN token             VARCHAR(255),
    ADD COLUMN tokenCreationDate TIMESTAMP;
