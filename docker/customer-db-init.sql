-- ============================================================================
-- CUSTOMER SERVICE DATABASE INITIALIZATION
-- ============================================================================

\c customer_db;

-- ============================================================================
-- TABLE: persons
-- ============================================================================
CREATE TABLE IF NOT EXISTS persons (
    person_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    gender VARCHAR(10) NOT NULL CHECK (gender IN ('Male', 'Female', 'Other')),
    identification VARCHAR(20) NOT NULL UNIQUE,
    address VARCHAR(200) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_persons_identification ON persons(identification);

-- ============================================================================
-- TABLE: customers
-- ============================================================================
CREATE TABLE IF NOT EXISTS customers (
    customer_id BIGSERIAL PRIMARY KEY,
    person_id BIGINT NOT NULL,
    password VARCHAR(255) NOT NULL,
    status BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_customer_person FOREIGN KEY (person_id) 
        REFERENCES persons(person_id) ON DELETE CASCADE
);

CREATE INDEX idx_customers_person_id ON customers(person_id);
CREATE INDEX idx_customers_status ON customers(status);

-- ============================================================================
-- TRIGGERS: Auto-update timestamps
-- ============================================================================
CREATE OR REPLACE FUNCTION update_persons_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_persons_updated_at
    BEFORE UPDATE ON persons
    FOR EACH ROW
    EXECUTE FUNCTION update_persons_timestamp();

CREATE OR REPLACE FUNCTION update_customers_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_customers_updated_at
    BEFORE UPDATE ON customers
    FOR EACH ROW
    EXECUTE FUNCTION update_customers_timestamp();

-- ============================================================================
-- SAMPLE DATA
-- ============================================================================

-- Insert persons
INSERT INTO persons (name, gender, identification, address, phone) VALUES
('Jose Lema', 'Male', '1234567890', 'Otavalo sn y principal', '098254785'),
('Marianela Montalvo', 'Female', '0987654321', 'Amazonas y NNUU', '097548965'),
('Juan Osorio', 'Male', '1122334455', '13 junio y Equinoccial', '098874587')
ON CONFLICT (identification) DO NOTHING;

-- Insert customers (passwords: 1234, 5678, 1245)
-- BCrypt hash for "1234": $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- BCrypt hash for "5678": $2a$10$XYZ...
-- BCrypt hash for "1245": $2a$10$ABC...
INSERT INTO customers (person_id, password, status) 
SELECT p.person_id, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true
FROM persons p WHERE p.identification = '1234567890'
ON CONFLICT DO NOTHING;

INSERT INTO customers (person_id, password, status) 
SELECT p.person_id, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true
FROM persons p WHERE p.identification = '0987654321'
ON CONFLICT DO NOTHING;

INSERT INTO customers (person_id, password, status) 
SELECT p.person_id, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true
FROM persons p WHERE p.identification = '1122334455'
ON CONFLICT DO NOTHING;

-- ============================================================================
-- VERIFICATION QUERY
-- ============================================================================
SELECT 
    c.customer_id,
    p.name,
    p.identification,
    c.status,
    c.created_at
FROM customers c
INNER JOIN persons p ON c.person_id = p.person_id;

COMMIT;