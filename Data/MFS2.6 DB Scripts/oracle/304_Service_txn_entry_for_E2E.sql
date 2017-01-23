
INSERT INTO SERVICE_TRANSACTION(VERSION,LASTUPDATETIME,UPDATEDBY,CREATETIME,CREATEDBY,MSPID,SERVICEID,TRANSACTIONTYPEID)  VALUES (1,SYSDATE,'System',SYSDATE,'System',1, (SELECT ID FROM SERVICE WHERE SERVICENAME = 'Wallet'), (SELECT ID FROM TRANSACTION_TYPE WHERE TRANSACTIONNAME = 'InterBankTransfer'));

INSERT INTO mfa_transactions_info VALUES (mfa_transactions_info_ID_SEQ.nextval,1,sysdate,'System',sysdate,'System',1,(select id from service where SERVICENAME = 'Wallet') ,(select id from transaction_type where TRANSACTIONNAME = 'E2ETransfer'),(select id from channel_code where CHANNELNAME = 'WebAPI'),1);

commit;