DROP TABLE IF EXISTS day_transactions;
DROP TABLE IF EXISTS processed_day_transactions;

CREATE TABLE day_transactions (
    id BIGINT PRIMARY KEY,
    description VARCHAR(255),
    amount DECIMAL(19,2),
    transaction_date TIMESTAMP,
    status VARCHAR(50)
);

CREATE TABLE processed_day_transactions (
    id BIGINT PRIMARY KEY,
    processed_description VARCHAR(255),
    processed_amount DECIMAL(19,2),
    processed_date TIMESTAMP,
    processed_status VARCHAR(50),
    processing_notes VARCHAR(500)
);

-- Insert sample data
INSERT INTO day_transactions (id, description, amount, transaction_date, status) VALUES
(1, 'Grocery Shopping', 150.50, CURRENT_TIMESTAMP(), 'PENDING'),
(2, 'Gas Station', 45.75, CURRENT_TIMESTAMP(), 'COMPLETED'),
(3, 'Online Purchase', 299.99, CURRENT_TIMESTAMP(), 'PENDING'),
(4, 'Restaurant Bill', 85.25, CURRENT_TIMESTAMP(), 'FAILED'),
(5, 'Movie Tickets', 35.00, CURRENT_TIMESTAMP(), 'PENDING'),
(6, 'Phone Bill', 89.99, CURRENT_TIMESTAMP(), 'PENDING'),
(7, 'Internet Service', 79.99, CURRENT_TIMESTAMP(), 'COMPLETED'),
(8, 'Coffee Shop', 12.50, CURRENT_TIMESTAMP(), 'PENDING'),
(9, 'Book Store', 45.99, CURRENT_TIMESTAMP(), 'PENDING'),
(10, 'Gym Membership', 65.00, CURRENT_TIMESTAMP(), 'COMPLETED');