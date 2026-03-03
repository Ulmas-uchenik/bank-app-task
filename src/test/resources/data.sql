-- Очистка таблиц (для повторных запусков)
DELETE FROM token;
DELETE FROM user_password;
DELETE FROM transactions;
DELETE FROM bank_accounts;
DELETE FROM users;

-- Сброс последовательности для H2
ALTER TABLE token ALTER COLUMN id RESTART WITH 1;

-- Вставка тестовых пользователей
INSERT INTO users (user_id, username, phone, email) VALUES
                                                        ('test-user-1', 'testuser1', '+71234567890', 'test1@example.com'),
                                                        ('test-user-2', 'testuser2', '+79876543210', 'test2@example.com'),
                                                        ('test-user-3', 'testuser3', '+71112223344', 'test3@example.com');

-- Вставка тестовых паролей пользователей (если нужно для аутентификации)
INSERT INTO user_password (email, role, password) VALUES
                                                      ('test1@example.com', 'USER', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG'),
                                                      ('test2@example.com', 'USER', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG'),
                                                      ('test3@example.com', 'ADMIN', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG');

-- Вставка тестовых банковских счетов
INSERT INTO bank_accounts (number, balance, currency, is_blocking, user_id) VALUES
                                                                                ('ACC-TEST-1', 1000.00, 'USD', false, 'test-user-1'),
                                                                                ('ACC-TEST-2', 500.00, 'EUR', false, 'test-user-1'),
                                                                                ('ACC-TEST-3', 2000.00, 'USD', true, 'test-user-2'),
                                                                                ('ACC-TEST-4', 750.00, 'RUB', false, 'test-user-3');

-- Вставка тестовых транзакций (используем CURRENT_TIMESTAMP() для H2)
INSERT INTO transactions (id, amount, type, date, source, target) VALUES
                                                                      ('trans-test-1', 100.00, 'TRANSFER', CURRENT_TIMESTAMP(), 'ACC-TEST-1', 'ACC-TEST-2'),
                                                                      ('trans-test-2', 50.00, 'PAYMENT', CURRENT_TIMESTAMP(), 'ACC-TEST-2', 'ACC-TEST-3'),
                                                                      ('trans-test-3', 200.00, 'TRANSFER', CURRENT_TIMESTAMP(), 'ACC-TEST-1', 'ACC-TEST-4');