ALTER TABLE mfsbiller_partner_map ADD ChargesIncluded Number(3,0);

ALTER TABLE bill_payments ADD ChargesIncluded Number(3,0);

commit;