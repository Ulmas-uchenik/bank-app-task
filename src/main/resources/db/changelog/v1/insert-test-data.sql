-- Вставка пользователей
INSERT INTO users (user_id, username, phone, email) VALUES
    ('user1', 'firstUser', '+1234567890', 'first@example.com'),
    ('user2', 'secondUser', '+0987654321', 'second@example.com');

-- Вставка банковских счетов для первого пользователя (2 карты)
INSERT INTO bank_accounts (number, balance, currency, is_blocking, user_id) VALUES
    ('ACC123456789', 1000.50, 'USD', false, 'user1'),
    ('ACC987654321', 2500.75, 'EUR', false, 'user1');

-- Вставка банковского счета для второго пользователя (1 карта)
INSERT INTO bank_accounts (number, balance, currency, is_blocking, user_id) VALUES
    ('ACC555555555', 500.00, 'USD', false, 'user2');

-- Вставка тестовых транзакций (опционально)
INSERT INTO transactions (id, amount, type, date, source, target) VALUES
    ('trans1', 100.00, 'TRANSFER', CURRENT_TIMESTAMP, 'ACC123456789', 'ACC987654321'),
    ('trans2', 50.00, 'PAYMENT', CURRENT_TIMESTAMP, 'ACC987654321', 'ACC555555555');