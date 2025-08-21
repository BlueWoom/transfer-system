-- Clean up existing data to ensure a fresh start for tests
TRUNCATE TABLE transfer_entity, account_entity RESTART IDENTITY;

-- =================================================================
--  ACCOUNTS DATA
-- =================================================================

-- Insert three sample accounts with different currencies and balances
-- Note: 'owner_id' is the business key used for relationships.
INSERT INTO account_entity (id, version, owner_id, currency, balance)
VALUES (1, 0, 101, 'EUR', 1000.0000),
       (2, 0, 102, 'EUR', 1000.0000),
       (3, 0, 103, 'EUR', 1000.0000);

COMMIT;