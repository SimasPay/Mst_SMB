INSERT INTO mfa_transactions_info(VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME,CREATEDBY,MSPID,SERVICEID,TRANSACTIONTYPEID,CHANNELCODEID,MFAMODE) VALUES (1,now(),'System',now(),'System',1,(select id from service where SERVICENAME = 'Bank')
,(select id from transaction_type where TRANSACTIONNAME = 'Transfer')
,(select id from channel_code where CHANNELNAME = 'WebAPI'),1);

INSERT INTO mfa_transactions_info(VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME,CREATEDBY,MSPID,SERVICEID,TRANSACTIONTYPEID,CHANNELCODEID,MFAMODE) VALUES (1,now(),'System',now(),'System',1,(select id from service where SERVICENAME = 'Payment')
,(select id from transaction_type where TRANSACTIONNAME = 'BillPay')
,(select id from channel_code where CHANNELNAME = 'WebAPI'),1);

INSERT INTO mfa_transactions_info(VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME,CREATEDBY,MSPID,SERVICEID,TRANSACTIONTYPEID,CHANNELCODEID,MFAMODE) VALUES (1,now(),'System',now(),'System',1,(select id from service where SERVICENAME = 'Buy')
,(select id from transaction_type where TRANSACTIONNAME = 'AirtimePurchase')
,(select id from channel_code where CHANNELNAME = 'WebAPI'),1);

INSERT INTO mfa_transactions_info(VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME,CREATEDBY,MSPID,SERVICEID,TRANSACTIONTYPEID,CHANNELCODEID,MFAMODE) VALUES (1,now(),'System',now(),'System',1,(select id from service where SERVICENAME = 'Bank')
,(select id from transaction_type where TRANSACTIONNAME = 'InterBankTransfer')
,(select id from channel_code where CHANNELNAME = 'WebAPI'),1);