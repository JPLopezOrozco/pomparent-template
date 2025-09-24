ALTER TABLE accounts ADD COLUMN user_id BIGINT;

UPDATE accounts SET user_id = 1 WHERE accounts.user_id IS NULL;

ALTER TABLE accounts ALTER COLUMN user_id SET NOT NULL;

CREATE INDEX IF NOT EXISTS idx_accounts_user_id ON accounts (user_id, created_at DESC);

ALTER TABLE accounts DROP COLUMN holder_name;