CREATE TABLE products (
    id          UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255)   NOT NULL,
    description TEXT,
    price       NUMERIC(10, 2) NOT NULL CHECK (price > 0),
    category_id UUID           NOT NULL REFERENCES categories (id),
    image_url   VARCHAR(500),
    active      BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP      NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP      NOT NULL DEFAULT now()
);

CREATE INDEX idx_products_category_id ON products (category_id);
CREATE INDEX idx_products_active       ON products (active);

-- Seed products for local development
INSERT INTO products (name, description, price, category_id)
SELECT 'Wireless Headphones', 'Noise-cancelling over-ear headphones', 79.99, id FROM categories WHERE slug = 'electronics'
UNION ALL
SELECT 'Running Shoes', 'Lightweight mesh running shoes', 59.99, id FROM categories WHERE slug = 'sports'
UNION ALL
SELECT 'Java Programming', 'Comprehensive Java guide for all levels', 39.99, id FROM categories WHERE slug = 'books';
