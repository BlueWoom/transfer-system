-- Clean up existing data to ensure a fresh start for tests
TRUNCATE TABLE transfer_entity, account_entity RESTART IDENTITY;

-- =================================================================
--  ACCOUNTS DATA
-- =================================================================

-- Insert three sample accounts with different currencies and balances
-- Note: 'owner_id' is the business key used for relationships.
INSERT INTO account_entity (id, owner_id, currency, balance)
VALUES (1, 101, 'EUR', 1000.0000),
       (2, 102, 'USD', 100.0000),
       (3, 103, 'EUR', 1000.0000),
       (4, 104, 'SGD', 1000.0000);