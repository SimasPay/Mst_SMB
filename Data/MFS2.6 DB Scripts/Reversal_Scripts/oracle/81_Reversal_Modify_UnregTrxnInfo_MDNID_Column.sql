DELETE FROM service_transaction where serviceid=(select id from service where servicename='Wallet') and transactiontypeid=(select id from transaction_type where transactionname='FundReversal');
DELETE FROM transaction_type where TRANSACTIONNAME = 'FundReversal';

Delete from notification where code = 801;


commit;