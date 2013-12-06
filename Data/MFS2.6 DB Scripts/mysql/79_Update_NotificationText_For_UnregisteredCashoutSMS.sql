

update notification set text = 'Sorry, transaction on $(TransactionDateTime) failed. Please verify TransferID and try again. Info, call $(CustomerServiceShortCode). REF: $(TransactionID)' where code='59';

update notification set text = 'ERROR: Incorrect Unregistered Subscriber details. Please verify Unregistered Subscriber Details and retry' where code='2025';