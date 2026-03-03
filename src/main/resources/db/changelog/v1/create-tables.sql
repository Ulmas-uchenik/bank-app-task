-- Создание таблицы users
CREATE TABLE IF NOT EXISTS users (
    user_id VARCHAR(255) PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE
    );

-- Создание таблицы bank_accounts
CREATE TABLE IF NOT EXISTS bank_accounts (
    number VARCHAR(255) PRIMARY KEY,
    balance DECIMAL(19, 2),
    currency VARCHAR(255),
    is_blocking BOOLEAN NOT NULL DEFAULT false,
    user_id VARCHAR(255),
    CONSTRAINT fk_bank_accounts_user FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

-- Создание таблицы transactions
CREATE TABLE IF NOT EXISTS transactions (
    id VARCHAR(255) PRIMARY KEY,
    amount DECIMAL(19, 2) NOT NULL,
    type VARCHAR(255) NOT NULL,
    date TIMESTAMP NOT NULL,
    source VARCHAR(255),
    target VARCHAR(255) NOT NULL,
    CONSTRAINT fk_transactions_source FOREIGN KEY (source) REFERENCES bank_accounts(number),
    CONSTRAINT fk_transactions_target FOREIGN KEY (target) REFERENCES bank_accounts(number)
    );

-- Создание таблицы user_password
CREATE TABLE IF NOT EXISTS user_password (
    email VARCHAR(255) PRIMARY KEY,
    role VARCHAR(255) NOT NULL,
    password VARCHAR(255)
    );

-- Создание таблицы token
CREATE TABLE IF NOT EXISTS token (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) UNIQUE,
    token_type VARCHAR(255),
    revoked BOOLEAN,
    expired BOOLEAN,
    user_password_email VARCHAR(255),
    CONSTRAINT fk_token_user FOREIGN KEY (user_password_email) REFERENCES user_password(email)
    );