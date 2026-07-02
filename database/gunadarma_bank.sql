CREATE DATABASE IF NOT EXISTS gunadarma_bank
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE gunadarma_bank;

CREATE TABLE users (
    user_id VARCHAR(12) NOT NULL,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id),
    CONSTRAINT uk_users_username UNIQUE (username)
) ENGINE=InnoDB;

CREATE TABLE accounts (
    account_number VARCHAR(20) NOT NULL,
    user_id VARCHAR(12) NOT NULL,
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    pin CHAR(6) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (account_number),
    CONSTRAINT uk_accounts_user UNIQUE (user_id),
    CONSTRAINT chk_accounts_balance CHECK (balance >= 0),
    CONSTRAINT chk_accounts_pin CHECK (pin REGEXP '^[0-9]{6}$')
) ENGINE=InnoDB;

CREATE TABLE transactions (
    transaction_id VARCHAR(20) NOT NULL,
    account_number VARCHAR(20) NOT NULL,
    transaction_type ENUM('DEPOSIT', 'WITHDRAW', 'TRANSFER_OUT', 'TRANSFER_IN') NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    from_account_number VARCHAR(20) NULL,
    to_account_number VARCHAR(20) NULL,
    description VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (transaction_id),
    CONSTRAINT chk_transactions_amount CHECK (amount > 0)
) ENGINE=InnoDB;

ALTER TABLE accounts
    ADD CONSTRAINT fk_accounts_user
    FOREIGN KEY (user_id) REFERENCES users(user_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;

ALTER TABLE transactions
    ADD CONSTRAINT fk_transactions_owner_account
    FOREIGN KEY (account_number) REFERENCES accounts(account_number)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;

ALTER TABLE transactions
    ADD CONSTRAINT fk_transactions_from_account
    FOREIGN KEY (from_account_number) REFERENCES accounts(account_number)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;

ALTER TABLE transactions
    ADD CONSTRAINT fk_transactions_to_account
    FOREIGN KEY (to_account_number) REFERENCES accounts(account_number)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;

ALTER TABLE transactions
    ADD INDEX idx_transactions_account_created (account_number, created_at),
    ADD INDEX idx_transactions_type (transaction_type),
    ADD INDEX idx_transactions_from_account (from_account_number),
    ADD INDEX idx_transactions_to_account (to_account_number);

INSERT INTO users (user_id, username, password, full_name) VALUES
    ('USR0001', 'budi', 'budi123', 'Budi Santoso'),
    ('USR0002', 'siti', 'siti123', 'Siti Rahayu');

INSERT INTO accounts (account_number, user_id, balance, pin) VALUES
    ('10000001', 'USR0001', 5000000.00, '123456'),
    ('10000002', 'USR0002', 3000000.00, '654321');
