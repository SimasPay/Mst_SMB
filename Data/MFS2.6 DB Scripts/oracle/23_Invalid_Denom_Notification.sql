DELETE FROM notification WHERE Code=2017;

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2017,'InvalidTopupDenomination',1,'ERROR: The Denomination $(BillAmountValue) is invalid. Valid Denominations are $(ValidDenominations) $(TransactionDateTime)  REF: $(TransactionID)',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2017,'InvalidTopupDenomination',2,'ERROR: The Denomination $(BillAmountValue) is invalid. Valid Denominations are $(ValidDenominations) $(TransactionDateTime)  REF: $(TransactionID)',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2017,'InvalidTopupDenomination',4,'ERROR: The Denomination $(BillAmountValue) is invalid. Valid Denominations are $(ValidDenominations) $(TransactionDateTime)  REF: $(TransactionID)',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2017,'InvalidTopupDenomination',8,'ERROR: The Denomination $(BillAmountValue) is invalid. Valid Denominations are $(ValidDenominations) $(TransactionDateTime)  REF: $(TransactionID)',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2017,'InvalidTopupDenomination',16,'ERROR: The Denomination $(BillAmountValue) is invalid. Valid Denominations are $(ValidDenominations) $(TransactionDateTime)  REF: $(TransactionID)',null,0,0,sysdate,null,null,1);

commit;