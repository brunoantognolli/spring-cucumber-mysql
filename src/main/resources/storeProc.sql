#Tables must have unique names
DROP TEMPORARY TABLE IF EXISTS tmpTransactionA;
DROP TEMPORARY TABLE IF EXISTS tmpTransactionB;
DROP PROCEDURE IF EXISTS ProcessDayTransactions;

CREATE PROCEDURE ProcessDayTransactions()
BEGIN
    CREATE TEMPORARY TABLE tmpTransactionA as
    SELECT 
        dt.id as transaction_id,
        dt.amount as original_amount,
        CASE 
            WHEN dt.status = 'PENDING' THEN dt.amount * 1.1
            WHEN dt.status = 'FAILED' THEN dt.amount * 1.15
            ELSE dt.amount
        END as processed_amount,
        CURRENT_TIMESTAMP() as processing_date,
        dt.status as original_status
    FROM day_transactions dt;

    -- Return the results
    CREATE TEMPORARY TABLE tmpTransactionB as
    SELECT transaction_id,
           original_amount,
           processed_amount,
           processed_amount*2 as new_processed_amount
    FROM tmpTransactionA;

    SELECT * FROM tmpTransactionB;
END;