-- Clean up existing data to ensure a fresh start for tests
TRUNCATE TABLE transfer_entity, account_entity RESTART IDENTITY;

-- =================================================================
--  ACCOUNTS DATA
-- =================================================================

-- Insert three sample accounts with different currencies and balances
-- Note: 'owner_id' is the business key used for relationships.
INSERT INTO account_entity (id, version, owner_id, currency, balance)
VALUES (1, 0, 101, 'EUR', 5000.0000),
       (2, 0, 102, 'USD', 2500.0000),
       (3, 0, 103, 'EUR', 10000.0000),
       (4, 0, 104, 'SGD', 10000.0000);


-- =================================================================
--  TRANSFERS DATA
-- =================================================================

-- 1. A successfully completed cross-currency transfer from owner 101 to 102
INSERT INTO transfer_entity (id,
                             version,
                             transfer_id,
                             request_id,
                             created_at,
                             transfer_amount,
                             originator_id,
                             beneficiary_id,
                             status,
                             processed_at,
                             exchange_rate,
                             debit,
                             credit)
VALUES (1001,
        0,
        'a1b2c3d4-e5f6-7890-1234-567890abcdef', -- transferId
        'f1e2d3c4-b5a6-9870-6543-210987fedcba', -- requestId
        '2025-08-15T10:00:00Z', -- createdAt
        100.00, -- transferAmount (in originator's currency)
        101, -- originator_id (EUR account)
        102, -- beneficiary_id (USD account)
        'SUCCESS', -- status
        '2025-08-15T10:00:15Z', -- processedAt
        1.0850000000, -- exchangeRate
        100.0000, -- debit (balance from originator)
        108.5000 -- credit (balance to beneficiary after conversion)
       );

-- 2. A failed transfer from owner 102 to 103 (e.g., due to a processing error)
INSERT INTO transfer_entity (id,
                             version,
                             transfer_id,
                             request_id,
                             created_at,
                             transfer_amount,
                             originator_id,
                             beneficiary_id,
                             status,
                             processed_at)
VALUES (1002,
        0,
        'c3d4e5f6-a7b8-9012-3456-7890abcdef12', -- transferId
        'd3c4b5a6-9870-6543-2109-876fedcba321', -- requestId
        '2025-08-16T09:00:00Z', -- createdAt
        200.00, -- transferAmount
        102, -- originator_id
        103, -- beneficiary_id
        'FAILED', -- status
        '2025-08-16T09:01:05Z' -- processedAt
           -- No debit/credit as the transaction failed and was rolled back
       );

COMMIT;