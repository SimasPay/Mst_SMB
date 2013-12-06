
ALTER TABLE bill_payments ADD (INTxnId VARCHAR(45));

ALTER TABLE bill_payments ADD (INResponseCode VARCHAR(45));

COMMIT;