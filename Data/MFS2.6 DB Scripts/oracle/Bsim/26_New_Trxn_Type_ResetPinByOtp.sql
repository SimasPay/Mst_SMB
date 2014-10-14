DELETE FROM service_transaction where serviceid=(select id from service where servicename='Account') and transactiontypeid=(select id from transaction_type where transactionname='ResetPinByOTP');
DELETE FROM transaction_type where TRANSACTIONNAME = 'ResetPinByOTP';
INSERT INTO transaction_type VALUES (transaction_type_id_seq.nextval,1,sysdate,'System',sysdate,'System',1,'ResetPinByOTP','Reset Pin By OTP');
INSERT INTO service_transaction VALUES (service_transaction_id_seq.nextval,1,sysdate,'System',sysdate,'System',1, (select id from service where SERVICENAME = 'Account'), (select id from transaction_type where TRANSACTIONNAME = 'ResetPinByOTP'),0);

commit;