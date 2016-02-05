
UPDATE transaction_type SET DISPLAYNAME='B2B Transfer' WHERE TRANSACTIONNAME='Transfer';

INSERT INTO transaction_type VALUES (transaction_type_id_seq.nextval,1,sysdate,'System',sysdate,'System',1,'B2ETransfer','B2E Transfer');

INSERT INTO transaction_type VALUES (transaction_type_id_seq.nextval,1,sysdate,'System',sysdate,'System',1,'E2ETransfer','E2E Transfer');

INSERT INTO transaction_type VALUES (transaction_type_id_seq.nextval,1,sysdate,'System',sysdate,'System',1,'E2BTransfer','E2B Transfer');

INSERT INTO service_transaction VALUES (service_transaction_id_seq.nextval,1,sysdate,'System',sysdate,'System',1, (select id from service where SERVICENAME = 'Bank'), (select id from transaction_type where TRANSACTIONNAME = 'B2ETransfer'),1);

INSERT INTO service_transaction VALUES (service_transaction_id_seq.nextval,1,sysdate,'System',sysdate,'System',1, (select id from service where SERVICENAME = 'Wallet'), (select id from transaction_type where TRANSACTIONNAME = 'E2ETransfer'),1);

INSERT INTO service_transaction VALUES (service_transaction_id_seq.nextval,1,sysdate,'System',sysdate,'System',1, (select id from service where SERVICENAME = 'Wallet'), (select id from transaction_type where TRANSACTIONNAME = 'E2BTransfer'),1);

DELETE FROM service_transaction WHERE serviceid=(SELECT id FROM service WHERE servicename='Wallet') AND transactiontypeid=(SELECT id FROM transaction_type WHERE TRANSACTIONNAME='Transfer');

COMMIT;