INSERT INTO mfa_transactions_info VALUES (mfa_transactions_info_ID_SEQ.nextval,1,sysdate,'System',sysdate,'System',1,(select id from service where SERVICENAME = 'Account'),(select id from transaction_type where TRANSACTIONNAME = 'ChangePIN'),(select id from channel_code where CHANNELNAME = 'WebAPI'),1);

INSERT INTO mfa_transactions_info VALUES (mfa_transactions_info_ID_SEQ.nextval,1,sysdate,'System',sysdate,'System',1,(select id from service where SERVICENAME = 'Account'),(select id from transaction_type where TRANSACTIONNAME = 'Activation'),(select id from channel_code where CHANNELNAME = 'WebAPI'),1);

INSERT INTO mfa_transactions_info VALUES (mfa_transactions_info_ID_SEQ.nextval,1,sysdate,'System',sysdate,'System',1,(select id from service where SERVICENAME = 'Account'),(select id from transaction_type where TRANSACTIONNAME = 'Reactivation'),(select id from channel_code where CHANNELNAME = 'WebAPI'),1);



commit;
