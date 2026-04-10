CREATE TABLE users (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20)  NOT NULL CHECK (role IN ('ADMIN', 'CUSTOMER')),
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT now()
);

-- Seed an admin account for local development (password: Admin1234!)
INSERT INTO users (email, password_hash, role)
VALUES (
    'admin@lab.com',
    '$2a$10$I66DLIN3XBNkj.XV2yjhYOWmnbLbDmX8uBQLjwy/3ZHElDHoRiKUy',
    'ADMIN'
);
