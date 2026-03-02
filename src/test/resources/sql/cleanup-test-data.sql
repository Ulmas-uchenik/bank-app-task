-- Очистка таблиц в правильном порядке (с учетом foreign key constraints)
DELETE FROM transactions;
DELETE FROM bank_accounts;
DELETE FROM users;