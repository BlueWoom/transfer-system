-- Clean up existing data to ensure a fresh start for tests
TRUNCATE TABLE transfer_entity, account_entity RESTART IDENTITY;

-- =================================================================
--  ACCOUNTS DATA
-- =================================================================

-- Insert three sample accounts with different currencies and balances
-- Note: 'owner_id' is the business key used for relationships.
INSERT INTO account_entity (id, version, owner_id, currency, balance)
VALUES (1, 1, 101, 'EUR', 5000.00),
       (2, 1, 102, 'USD', 2500.00),
       (3, 1, 103, 'GBP', 10000.00),
       (4, 1, 104, 'JPY', 1500000.00),
       (5, 1, 105, 'CAD', 7500.00),
       (6, 1, 106, 'AUD', 8250.75),
       (7, 1, 107, 'CHF', 12000.00),
       (8, 1, 108, 'CNY', 50000.00),
       (9, 1, 109, 'SEK', 65000.00),
       (10, 1, 110, 'NZD', 9800.50);

COMMIT;