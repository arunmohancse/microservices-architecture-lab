CREATE TABLE inventory_items (
    id                   UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id           UUID      NOT NULL UNIQUE REFERENCES products (id),
    quantity_on_hand     INTEGER   NOT NULL DEFAULT 0 CHECK (quantity_on_hand >= 0),
    reserved_quantity    INTEGER   NOT NULL DEFAULT 0 CHECK (reserved_quantity >= 0),
    low_stock_threshold  INTEGER   NOT NULL DEFAULT 10,
    updated_at           TIMESTAMP NOT NULL DEFAULT now()
);

-- Seed initial stock for the seeded products
INSERT INTO inventory_items (product_id, quantity_on_hand, low_stock_threshold)
SELECT id, 50, 5 FROM products WHERE name = 'Wireless Headphones'
UNION ALL
SELECT id, 30, 10 FROM products WHERE name = 'Running Shoes'
UNION ALL
SELECT id, 100, 20 FROM products WHERE name = 'Java Programming';
