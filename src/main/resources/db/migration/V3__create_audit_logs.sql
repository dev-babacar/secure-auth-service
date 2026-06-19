CREATE TABLE audit_logs (
                            id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            user_id     UUID REFERENCES users(id),
                            event       VARCHAR(100) NOT NULL,
                            ip_address  VARCHAR(45),
                            created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);