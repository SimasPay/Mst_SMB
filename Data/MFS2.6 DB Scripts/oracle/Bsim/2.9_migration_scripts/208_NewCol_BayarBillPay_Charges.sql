ALTER TABLE bill_payments ADD OperatorCharges Number(25,4) ;
ALTER TABLE bill_payments ADD BillData CLOB DEFAULT NULL;