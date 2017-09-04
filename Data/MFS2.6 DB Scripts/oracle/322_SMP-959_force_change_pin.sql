DELETE FROM service_transaction where serviceid=(select id from service where servicename='Account') and transactiontypeid=(select id from transaction_type where transactionname='ForceChangePIN');
DELETE FROM transaction_type where TRANSACTIONNAME = 'ForceChangePIN';
DELETE FROM mfa_transactions_info where transactiontypeid = (select id from transaction_type where TRANSACTIONNAME = 'ForceChangePIN') and serviceid=(select id from service where servicename='Account') ;

INSERT INTO transaction_type VALUES (transaction_type_id_seq.nextval, 1, sysdate, 'System', sysdate, 'System', 1, 'ForceChangePIN', 'Force Change Pin By OTP');
INSERT INTO service_transaction VALUES (service_transaction_id_seq.nextval, 1, sysdate, 'System', sysdate, 'System', 1, (select id from service where SERVICENAME = 'Account'), (select id from transaction_type where TRANSACTIONNAME = 'ForceChangePIN'),0);

INSERT INTO mfa_transactions_info VALUES (mfa_transactions_info_ID_SEQ.nextval, 1, sysdate,'System', sysdate, 'System', 1, (select id from service where SERVICENAME = 'Account')
,(select id from transaction_type where TRANSACTIONNAME = 'ForceChangePIN'),(select id from channel_code where CHANNELNAME = 'WebAPI'),1);

commit;