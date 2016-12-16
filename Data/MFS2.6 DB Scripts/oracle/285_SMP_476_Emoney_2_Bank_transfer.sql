INSERT INTO TRANSACTION_TYPE VALUES (TRANSACTION_TYPE_ID_SEQ.NEXTVAL,1,SYSDATE,'System',SYSDATE,'System',1,'E2BTransfer','E2B Transfer');

INSERT INTO SERVICE_TRANSACTION(VERSION,LASTUPDATETIME,UPDATEDBY,CREATETIME,CREATEDBY,MSPID,SERVICEID,TRANSACTIONTYPEID)  VALUES (1,SYSDATE,'System',SYSDATE,'System',1, (SELECT ID FROM SERVICE WHERE SERVICENAME = 'Wallet'), (SELECT ID FROM TRANSACTION_TYPE WHERE TRANSACTIONNAME = 'E2BTransfer'));

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TransactionUICategory','5636','65','Emoney_To_Bank','Emoney To Bank');

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','BankAccountType','5184','00','UnSpecified','Un Specified');

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','BankAccountCardType','5202','00','UnSpecified','Un Specified');

INSERT INTO mfa_transactions_info VALUES (mfa_transactions_info_ID_SEQ.nextval,1,sysdate,'System',sysdate,'System',1,(select id from service where SERVICENAME = 'Wallet')
,(select id from transaction_type where TRANSACTIONNAME = 'E2BTransfer'),(select id from channel_code where CHANNELNAME = 'WebAPI'),1);


commit;