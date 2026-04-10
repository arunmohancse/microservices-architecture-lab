CREATE TABLE categories (
    id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name       VARCHAR(100) NOT NULL,
    slug       VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP    NOT NULL DEFAULT now()
);

-- Seed categories for local development
INSERT INTO categories (name, slug) VALUES
    ('Electronics',     'electronics'),
    ('Clothing',        'clothing'),
    ('Home & Garden',   'home-garden'),
    ('Books',           'books'),
    ('Sports',          'sports');
