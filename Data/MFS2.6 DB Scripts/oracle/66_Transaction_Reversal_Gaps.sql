Delete from service_transaction where SERVICEID = (select id from service where SERVICENAME = 'Bank') and TRANSACTIONTYPEID = (select id from transaction_type where TRANSACTIONNAME = 'ReverseTransaction');

INSERT INTO service_transaction VALUES (service_transaction_id_seq.nextval,1,sysdate,'System',sysdate,'System',1, (select id from service where SERVICENAME = 'Bank'), (select id from transaction_type where TRANSACTIONNAME = 'ReverseTransaction'));

commit;