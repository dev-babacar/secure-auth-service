CREATE TABLE users (
                       id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       email       VARCHAR(255) NOT NULL UNIQUE,
                       password    VARCHAR(255) NOT NULL,
                       role        VARCHAR(50)  NOT NULL DEFAULT 'USER',
                       mfa_enabled BOOLEAN      NOT NULL DEFAULT FALSE,
                       created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);