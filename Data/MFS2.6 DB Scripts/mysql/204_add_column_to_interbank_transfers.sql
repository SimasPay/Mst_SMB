ALTER TABLE interbank_transfers ADD COLUMN DestBankName VARCHAR(255);

update interbank_transfers ibt set ibt.DestBankName = (select ibc.bankname from interbank_codes ibc where ibc.bankcode=ibt.DestBankCode);