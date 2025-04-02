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

--Create the stored procedure
DELIMITER //

CREATE PROCEDURE ProcessDayTransactions()
BEGIN
    -- Create temporary table to store results
    CREATE TEMPORARY TABLE IF NOT EXISTS temp_processed_transactions (
        transaction_id BIGINT,
        original_amount DECIMAL(10,2),
        processed_amount DECIMAL(10,2),
        processing_date TIMESTAMP,
        original_status VARCHAR(20),
        processed_status VARCHAR(20),
        processing_notes VARCHAR(255)
    );

    -- Clear temporary table if it has data
    TRUNCATE TABLE temp_processed_transactions;

    -- Insert processed data into temporary table
    INSERT INTO temp_processed_transactions
    SELECT 
        dt.id as transaction_id,
        dt.amount as original_amount,
        CASE 
            WHEN dt.status = 'PENDING' THEN dt.amount * 1.1  -- Add 10% fee for pending transactions
            WHEN dt.status = 'FAILED' THEN dt.amount * 1.15  -- Add 15% fee for failed transactions
            ELSE dt.amount                                   -- No fee for completed transactions
        END as processed_amount,
        CURRENT_TIMESTAMP() as processing_date,
        dt.status as original_status,
        CASE 
            WHEN dt.status = 'PENDING' THEN 'IN_PROCESS'
            WHEN dt.status = 'FAILED' THEN 'RETRY'
            ELSE dt.status 
        END as processed_status,
        CASE 
            WHEN dt.status = 'PENDING' THEN 'Added 10% processing fee'
            WHEN dt.status = 'FAILED' THEN 'Added 15% retry fee'
            ELSE 'No additional processing needed'
        END as processing_notes
    FROM day_transactions dt
    WHERE dt.status IN ('PENDING', 'FAILED');

    -- Return the results
    SELECT * FROM temp_processed_transactions;

    -- Optional: Drop temporary table
    -- DROP TEMPORARY TABLE IF EXISTS temp_processed_transactions;
END //

DELIMITER ; 