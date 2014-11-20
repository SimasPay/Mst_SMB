INSERT INTO transaction_type VALUES (9,1,sysdate,'System',sysdate,'System',1,'SubscriberRegistration','SubscriberRegistration');
INSERT INTO service_transaction VALUES (service_transaction_id_seq.nextval,1,sysdate,'System',sysdate,'System',1, (select id from service where SERVICENAME = 'Account'), (select id from transaction_type where TRANSACTIONNAME = 'SubscriberRegistration'),0);

INSERT INTO transaction_type VALUES (16,1,sysdate,'System',sysdate,'System',1,'ChargeSettlement','Charge Settlement');
INSERT INTO service_transaction VALUES (service_transaction_id_seq.nextval,1,sysdate,'System',sysdate,'System',1, (select id from service where SERVICENAME = 'System'), (select id from transaction_type where TRANSACTIONNAME = 'ChargeSettlement'),0);

INSERT INTO transaction_type VALUES (18,1,sysdate,'System',sysdate,'System',1,'ReverseTransaction','Reverse Transaction');
INSERT INTO service_transaction VALUES (service_transaction_id_seq.nextval,1,sysdate,'System',sysdate,'System',1, (select id from service where SERVICENAME = 'Wallet'), (select id from transaction_type where TRANSACTIONNAME = 'ReverseTransaction'),0);
INSERT INTO service_transaction VALUES (service_transaction_id_seq.nextval,1,sysdate,'System',sysdate,'System',1, (select id from service where SERVICENAME = 'Bank'), (select id from transaction_type where TRANSACTIONNAME = 'ReverseTransaction'),0);

INSERT INTO transaction_type VALUES (28,1,sysdate,'System',sysdate,'System',1,'BillInquiry','BillInquiry');
INSERT INTO service_transaction VALUES (service_transaction_id_seq.nextval,1,sysdate,'System',sysdate,'System',1, (select id from service where SERVICENAME = 'Payment'), (select id from transaction_type where TRANSACTIONNAME = 'BillInquiry'),0);

commit;