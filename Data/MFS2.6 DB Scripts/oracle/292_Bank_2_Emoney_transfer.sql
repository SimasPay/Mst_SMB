INSERT INTO TRANSACTION_TYPE VALUES (TRANSACTION_TYPE_ID_SEQ.NEXTVAL,1,SYSDATE,'System',SYSDATE,'System',1,'B2ETransfer','B2E Transfer');

INSERT INTO SERVICE_TRANSACTION(VERSION,LASTUPDATETIME,UPDATEDBY,CREATETIME,CREATEDBY,MSPID,SERVICEID,TRANSACTIONTYPEID)  VALUES (1,SYSDATE,'System',SYSDATE,'System',1, (SELECT ID FROM SERVICE WHERE SERVICENAME = 'Bank'), (SELECT ID FROM TRANSACTION_TYPE WHERE TRANSACTIONNAME = 'B2ETransfer'));

INSERT INTO mfa_transactions_info VALUES (mfa_transactions_info_ID_SEQ.nextval,1,sysdate,'System',sysdate,'System',1,(select id from service where SERVICENAME = 'Bank')
,(select id from transaction_type where TRANSACTIONNAME = 'B2ETransfer'),(select id from channel_code where CHANNELNAME = 'WebAPI'),1);

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TransactionUICategory','5636','67','Bank_To_Emoney','Bank To Emoney');

commit;