-- ============================================================================
-- ACCOUNT SERVICE DATABASE INITIALIZATION
-- ============================================================================

\c account_db;

-- ============================================================================
-- TABLE: accounts
-- ============================================================================
CREATE TABLE IF NOT EXISTS accounts (
    account_id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(12) NOT NULL UNIQUE,
    account_type VARCHAR(20) NOT NULL CHECK (account_type IN ('Ahorro', 'Corriente')),
    initial_balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    current_balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    status BOOLEAN NOT NULL DEFAULT true,
    customer_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_initial_balance CHECK (initial_balance >= 0),
    CONSTRAINT chk_current_balance CHECK (current_balance >= 0)
);

CREATE INDEX idx_accounts_account_number ON accounts(account_number);
CREATE INDEX idx_accounts_customer_id ON accounts(customer_id);
CREATE INDEX idx_accounts_status ON accounts(status);

-- ============================================================================
-- TABLE: movements
-- ============================================================================
CREATE TABLE IF NOT EXISTS movements (
    movement_id BIGSERIAL PRIMARY KEY,
    movement_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    movement_type VARCHAR(10) NOT NULL CHECK (movement_type IN ('CREDIT', 'DEBIT')),
    amount DECIMAL(15,2) NOT NULL,
    balance DECIMAL(15,2) NOT NULL,
    account_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_movement_account FOREIGN KEY (account_id) 
        REFERENCES accounts(account_id) ON DELETE CASCADE,
    CONSTRAINT chk_amount CHECK (amount > 0)
);

CREATE INDEX idx_movements_account_id ON movements(account_id);
CREATE INDEX idx_movements_date ON movements(movement_date);
CREATE INDEX idx_movements_type ON movements(movement_type);

-- ============================================================================
-- TRIGGERS: Auto-update timestamps
-- ============================================================================
CREATE OR REPLACE FUNCTION update_accounts_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_accounts_updated_at
    BEFORE UPDATE ON accounts
    FOR EACH ROW
    EXECUTE FUNCTION update_accounts_timestamp();

-- ============================================================================
-- SAMPLE DATA
-- ============================================================================

-- Insert accounts (customer_ids 1, 2, 3 from customer-service)
INSERT INTO accounts (account_number, account_type, initial_balance, current_balance, status, customer_id) VALUES
('478758', 'Ahorro', 2000.00, 2000.00, true, 1),
('225487', 'Corriente', 100.00, 100.00, true, 2),
('495878', 'Ahorro', 0.00, 0.00, true, 3),
('496825', 'Ahorro', 540.00, 540.00, true, 2),
('585545', 'Corriente', 1000.00, 1000.00, true, 1)
ON CONFLICT (account_number) DO NOTHING;

-- Insert sample movements
INSERT INTO movements (movement_date, movement_type, amount, balance, account_id) 
SELECT '2024-02-10 10:00:00', 'DEBIT', 575.00, 1425.00, account_id 
FROM accounts WHERE account_number = '478758';

UPDATE accounts SET current_balance = 1425.00 WHERE account_number = '478758';

INSERT INTO movements (movement_date, movement_type, amount, balance, account_id) 
SELECT '2024-02-10 11:00:00', 'CREDIT', 600.00, 700.00, account_id 
FROM accounts WHERE account_number = '225487';

UPDATE accounts SET current_balance = 700.00 WHERE account_number = '225487';

INSERT INTO movements (movement_date, movement_type, amount, balance, account_id) 
SELECT '2024-02-10 12:00:00', 'CREDIT', 150.00, 150.00, account_id 
FROM accounts WHERE account_number = '495878';

UPDATE accounts SET current_balance = 150.00 WHERE account_number = '495878';

INSERT INTO movements (movement_date, movement_type, amount, balance, account_id) 
SELECT '2024-02-08 09:00:00', 'DEBIT', 540.00, 0.00, account_id 
FROM accounts WHERE account_number = '496825';

UPDATE accounts SET current_balance = 0.00 WHERE account_number = '496825';

-- ============================================================================
-- VERIFICATION QUERY
-- ============================================================================
SELECT 
    a.account_number,
    a.account_type,
    a.initial_balance,
    a.current_balance,
    a.customer_id,
    COUNT(m.movement_id) as total_movements
FROM accounts a
LEFT JOIN movements m ON a.account_id = m.account_id
GROUP BY a.account_id, a.account_number, a.account_type, a.initial_balance, a.current_balance, a.customer_id
ORDER BY a.account_number;

COMMIT;