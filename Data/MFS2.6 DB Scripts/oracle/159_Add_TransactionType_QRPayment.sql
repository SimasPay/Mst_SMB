DELETE FROM service_transaction where serviceid=(select id from service where servicename='Payment') and transactiontypeid=(select id from transaction_type where transactionname='QRPayment');
DELETE FROM transaction_type where TRANSACTIONNAME = 'QRPayment';
INSERT INTO transaction_type VALUES (transaction_type_id_seq.nextval,1,sysdate,'System',sysdate,'System',1,'QRPayment','QR Payment');
INSERT INTO service_transaction VALUES (service_transaction_id_seq.nextval,1,sysdate,'System',sysdate,'System',1, (select id from service where SERVICENAME = 'Payment'), (select id from transaction_type where TRANSACTIONNAME = 'QRPayment'),0);

commit;