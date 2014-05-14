ALTER TABLE interbank_transfers ADD DestBankName VARCHAR(255);

update interbank_transfers set DestBankName = (select bankname from interbank_codes where bankcode=DestBankCode);

commit;