
ALTER TABLE bill_payments ADD(PartnerBillerCode VARCHAR2(255));

ALTER TABLE bill_payments ADD(IntegrationCode VARCHAR2(255));

COMMIT;