#Tables must have unique names
DROP TEMPORARY TABLE IF EXISTS tmpTransaction;
DROP TEMPORARY TABLE IF EXISTS tmpTransaction2;

CREATE TEMPORARY TABLE tmpTransaction as
    SELECT
        dt.id as transaction_id,
        dt.amount as original_amount,
        CASE
            WHEN dt.status = 'PENDING' THEN dt.amount * :pendingMultiplier
            WHEN dt.status = 'FAILED' THEN dt.amount * :failedMultiplier
            ELSE dt.amount
        END as processed_amount,
        CURRENT_TIMESTAMP() as processing_date,
        dt.status as original_status
    FROM day_transactions dt;

CREATE TEMPORARY TABLE tmpTransaction2 as
    SELECT transaction_id,
           original_amount,
           processed_amount,
           processed_amount*2 as new_processed_amount
    FROM tmpTransaction;

SELECT * FROM tmpTransaction2;