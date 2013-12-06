ALTER TABLE mfsbiller_partner_map
ADD ChargesIncluded BOOLEAN;
ALTER TABLE bill_payments
ADD ChargesIncluded BOOLEAN;