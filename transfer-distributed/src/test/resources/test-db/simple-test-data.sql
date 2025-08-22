-- Clean up existing data to ensure a fresh start for tests
TRUNCATE TABLE request_entity RESTART IDENTITY;

-- =================================================================
--  REQUEST DATA
-- =================================================================

-- 1. A successfully completed request
INSERT INTO request_entity (id,
                            version,
                            transfer_id,
                            request_id)
VALUES (1001,
        0,
        'a1b2c3d4-e5f6-7890-1234-567890abcdef', -- transferId)
        'd3c4b5a6-9870-6543-2109-876fedcba321'  -- requestId
       );

COMMIT;