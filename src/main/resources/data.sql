-- Sample historical snapshots for local testing.
-- Loaded automatically by Spring Boot on startup.

DROP TABLE IF EXISTS asset_price_snapshots CASCADE;

CREATE TABLE asset_price_snapshots (
    id BIGSERIAL PRIMARY KEY,
    symbol VARCHAR(16) NOT NULL,
    captured_at TIMESTAMP WITH TIME ZONE NOT NULL,
    price_usd NUMERIC(19, 8) NOT NULL
);

CREATE INDEX idx_asset_price_symbol_captured_at
    ON asset_price_snapshots (symbol, captured_at);

INSERT INTO asset_price_snapshots (symbol, captured_at, price_usd) VALUES
('BTC', '2024-01-01T00:00:00Z', 42000.00000000),
('BTC', '2007-06-01T00:00:00Z', 56000.00000000),
('BTC', '2024-01-01T00:00:00Z', 60000.00000000),
('ETH', '2024-01-01T00:00:00Z', 2200.00000000),
('ETH', '2024-06-01T00:00:00Z', 3100.00000000),
('ETH', '2012-01-01T00:00:00Z', 3500.00000000),
('ADA', '2024-01-01T00:00:00Z', 0.45000000),
('ADA', '2024-06-01T00:00:00Z', 0.52000000),
('ADA', '2026-01-01T00:00:00Z', 0.78000000),
('XRP', '2024-01-01T00:00:00Z', 0.61000000),
('XRP', '2024-06-01T00:00:00Z', 0.54000000),
('XRP', '2012-01-01T00:00:00Z', 0.68000000);
