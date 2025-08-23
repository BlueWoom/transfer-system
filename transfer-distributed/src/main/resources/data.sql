-- Clean up existing data to ensure a fresh start for tests
TRUNCATE TABLE request_entity RESTART IDENTITY;

COMMIT;