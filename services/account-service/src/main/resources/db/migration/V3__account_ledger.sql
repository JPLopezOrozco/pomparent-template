CREATE TABLE account_ledger (
    id               BIGSERIAL PRIMARY KEY,
    account_id       BIGINT        NOT NULL,
    transaction_id   BIGINT        NOT NULL,
    amount           DECIMAL(19,4) NOT NULL CHECK (amount > 0),
    currency         VARCHAR(3)    NOT NULL,
    type VARCHAR(6)    NOT NULL CHECK (type IN ('DEBIT','CREDIT')),
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT now(),
    UNIQUE (transaction_id, account_id, type)
);

CREATE INDEX idx_ledger_account_created ON account_ledger (account_id, created_at DESC);
CREATE INDEX idx_ledger_tx              ON account_ledger (transaction_id);

ALTER TABLE account_ledger
    ADD CONSTRAINT fk_ledger_account
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE RESTRICT;