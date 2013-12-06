SET DEFINE OFF;

SET SCAN OFF;

Alter Table unregistered_txn_info add TransactionName varchar2(255 CHAR);
Alter Table unregistered_txn_info add Amount Number(25, 4);
Alter Table unregistered_txn_info add FailureReason varchar2(255 CHAR);

-- Add CashOutAtATM Transaction name
INSERT INTO transaction_type VALUES (transaction_type_id_seq.nextval,1,sysdate,'System',sysdate,'System',1,'CashOutAtATM','Cash Out At ATM');
INSERT INTO service_transaction VALUES (service_transaction_id_seq.nextval,1,sysdate,'System',sysdate,'System',1, (select id from service where SERVICENAME = 'Wallet'), (select id from transaction_type where TRANSACTIONNAME = 'CashOutAtATM'));

-- Creating Third Party Partner with Suspense Pcoket
INSERT INTO address VALUES (address_id_seq.nextval,1,sysdate,'System',sysdate,'System',NULL,'temp','temp','temp','temp','temp','temp',NULL);

INSERT INTO subscriber VALUES (subscriber_id_seq.nextval,1,sysdate,'System',sysdate,'System',1,1,0,'Third Party','Partner',NULL,NULL,NULL,NULL,'temp@temp.com',3,0,'NGN','WAT',0,2,1,sysdate,NULL,sysdate,'Approver','approved','System',sysdate,0,NULL,NULL,NULL,NULL,NULL,3,NULL,2,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL);

INSERT INTO subscriber_mdn VALUES (subscriber_mdn_id_seq.nextval,1,sysdate,'System',sysdate,'System', subscriber_id_seq.currval,'2343000',NULL,NULL,NULL,NULL,NULL,1,NULL,0,0,'C22815F5149873A9C024B40C9AF35F40AAE47270450E86771ACDB3A627244282',NULL,NULL,sysdate,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,0);

INSERT INTO mfino_user VALUES (mfino_user_id_seq.nextval,1,sysdate,'System',sysdate,'System',1,1,'ThirdParty','0c3540b6a792673970f13f4e67d22946438e0055','Third Party','Partner','temp@temp.com',0,'WAT',0,0,sysdate,0,1,NULL,NULL,23,NULL,NULL,NULL,sysdate,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);

INSERT INTO partner VALUES (partner_id_seq.nextval,1,sysdate,'System',sysdate,'System',subscriber_id_seq.currval, mfino_user_id_seq.currval,1,'ThirdParty',1,'ThirdParty','Cooperative','','','','mfino','','1000',address_id_seq.currval,address_id_seq.currval,'',NULL,'temp',2011,'','temp@temp.com',1);

INSERT INTO pocket VALUES (pocket_id_seq.nextval,1,sysdate,'System',sysdate,'System',3,subscriber_mdn_id_seq.currval,NULL,'/YST2/P0lVQ=',0.0000,0.0000,0.0000,0,0,0,NULL,NULL,NULL,'wl1Hb4D+Yoir2xJh5ykMZw==',0,1,1,sysdate,sysdate,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);
INSERT INTO pocket VALUES (pocket_id_seq.nextval,1,sysdate,'System',sysdate,'System',4,subscriber_mdn_id_seq.currval,NULL,'/YST2/P0lVQ=',0.0000,0.0000,0.0000,0,0,0,NULL,NULL,NULL,'wl1Hb4D+YojgaFXBro8DAw==',0,1,1,sysdate,sysdate,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);
INSERT INTO pocket VALUES (pocket_id_seq.nextval,1,sysdate,'System',sysdate,'System',5,subscriber_mdn_id_seq.currval,NULL,'/YST2/P0lVQ=',0.0000,0.0000,0.0000,0,0,0,NULL,NULL,NULL,'wl1Hb4D+YojLNF658UegyA==',0,1,1,sysdate,sysdate,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);
INSERT INTO pocket VALUES (pocket_id_seq.nextval,1,sysdate,'System',sysdate,'System',7,subscriber_mdn_id_seq.currval,NULL,'/YST2/P0lVQ=',0.0000,0.0000,0.0000,0,0,0,NULL,NULL,NULL,'wl1Hb4D+YohfQ01SlmdqHg==',0,1,1,sysdate,sysdate,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);

INSERT INTO partner_services VALUES (partner_services_id_seq.nextval,1,sysdate,'System',sysdate,'System',1,partner_id_seq.currval,(select id from partner where BUSINESSPARTNERTYPE = 0),(select id from service where SERVICENAME = 'Account'),NULL,NULL,NULL,1,(pocket_id_seq.currval-1),(pocket_id_seq.currval-3),2,(pocket_id_seq.currval-3));
INSERT INTO partner_services VALUES (partner_services_id_seq.nextval,1,sysdate,'System',sysdate,'System',1,partner_id_seq.currval,(select id from partner where BUSINESSPARTNERTYPE = 0),(select id from service where SERVICENAME = 'Wallet'),NULL,NULL,NULL,1,(pocket_id_seq.currval-1),(pocket_id_seq.currval-3),2,(pocket_id_seq.currval-3));
INSERT INTO partner_services VALUES (partner_services_id_seq.nextval,1,sysdate,'System',sysdate,'System',1,partner_id_seq.currval,(select id from partner where BUSINESSPARTNERTYPE = 0),(select id from service where SERVICENAME = 'Bank'),NULL,NULL,NULL,1,(pocket_id_seq.currval-1),(pocket_id_seq.currval-3),2,(pocket_id_seq.currval-3));

INSERT INTO settlement_template VALUES (settlement_template_id_seq.nextval,1,sysdate,'System',sysdate,'System',1,'ThirdParty_daily',(pocket_id_seq.currval-3),1,partner_id_seq.currval);

INSERT INTO service_settlement_cfg VALUES (service_settlement_cfg_id_seq.nextval,1,sysdate,'System',sysdate,'System',1,settlement_template_id_seq.currval,(partner_services_id_seq.currval-2),NULL,NULL,1,0,(pocket_id_seq.currval-1),NULL);
INSERT INTO service_settlement_cfg VALUES (service_settlement_cfg_id_seq.nextval,1,sysdate,'System',sysdate,'System',1,settlement_template_id_seq.currval,(partner_services_id_seq.currval-1),NULL,NULL,1,0,(pocket_id_seq.currval-1),NULL);
INSERT INTO service_settlement_cfg VALUES (service_settlement_cfg_id_seq.nextval,1,sysdate,'System',sysdate,'System',1,settlement_template_id_seq.currval,partner_services_id_seq.currval,NULL,NULL,1,0,(pocket_id_seq.currval-1),NULL);

-- Insert New System Parameters
Delete from system_parameters where ParameterName = 'thirdparty.partner.mdn';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','thirdparty.partner.mdn','2343000','Third Party Partner MDN');

Delete from system_parameters where ParameterName = 'fac.prefix.value';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','fac.prefix.value','12345','Fund Access Code Prefix Value');

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TransactionUICategory','5636','50','Cashout_At_ATM','Cashout At ATM');

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TransactionUICategory','5636','51','Withdraw_From_ATM','Withdraw From ATM');

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','UnRegisteredTxnStatus','6541','7','CASHOUT_EXPIRED','Cash Out Expired');

Delete from notification where code = 708;
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,708,'CashOutAtATMConfirmationPrompt',1,'You requested to cash out amount $(Currency) $(Amount) from ATM and Service Charge $(Currency) $(serviceCharge).',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,708,'CashOutAtATMConfirmationPrompt',2,'You requested to cash out amount $(Currency) $(Amount) from ATM and Service Charge $(Currency) $(serviceCharge).',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,708,'CashOutAtATMConfirmationPrompt',4,'You requested to cash out amount $(Currency) $(Amount) from ATM and Service Charge $(Currency) $(serviceCharge).',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,708,'CashOutAtATMConfirmationPrompt',8,'You requested to cash out amount $(Currency) $(Amount) from ATM and Service Charge $(Currency) $(serviceCharge).',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,708,'CashOutAtATMConfirmationPrompt',16,'You requested to cash out amount $(Currency) $(Amount) from ATM and Service Charge $(Currency) $(serviceCharge).',null,0,0,sysdate,null,null,1);

Delete from notification where code = 709;
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,709,'CashOutAtATMConfirmedToSender',1,'Transaction ID: $(TransferID). Your Cash out Request for amount $(Currency) $(Amount) is successful with Service Charge $(Currency) $(serviceCharge). Please visit nearest ATM for cash out. And Fund access code for the same is $(OneTimePin).',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,709,'CashOutAtATMConfirmedToSender',2,'Transaction ID: $(TransferID). Your Cash out Request for amount $(Currency) $(Amount) is successful with Service Charge $(Currency) $(serviceCharge). Please visit nearest ATM for cash out. And Fund access code for the same is $(OneTimePin).',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,709,'CashOutAtATMConfirmedToSender',4,'Transaction ID: $(TransferID). Your Cash out Request for amount $(Currency) $(Amount) is successful with Service Charge $(Currency) $(serviceCharge). Please visit nearest ATM for cash out. And Fund access code for the same is $(OneTimePin).',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,709,'CashOutAtATMConfirmedToSender',8,'Transaction ID: $(TransferID). Your Cash out Request for amount $(Currency) $(Amount) is successful with Service Charge $(Currency) $(serviceCharge). Please visit nearest ATM for cash out. And Fund access code for the same is $(OneTimePin).',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,709,'CashOutAtATMConfirmedToSender',16,'Transaction ID: $(TransferID). Your Cash out Request for amount $(Currency) $(Amount) is successful with Service Charge $(Currency) $(serviceCharge). Please visit nearest ATM for cash out. And Fund access code for the same is $(OneTimePin).',null,0,0,sysdate,null,null,1);

Delete from notification where code = 710;
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,710,'CashOutAtATMExpired',1,'Your Cash out request is expired. And the amount will be reverted to your Eoney wallet.',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,710,'CashOutAtATMExpired',2,'Your Cash out request is expired. And the amount will be reverted to your Eoney wallet.',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,710,'CashOutAtATMExpired',4,'Your Cash out request is expired. And the amount will be reverted to your Eoney wallet.',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,710,'CashOutAtATMExpired',8,'Your Cash out request is expired. And the amount will be reverted to your Eoney wallet.',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,710,'CashOutAtATMExpired',16,'Your Cash out request is expired. And the amount will be reverted to your Eoney wallet.',null,0,0,sysdate,null,null,1);

Delete from notification where code = 711;
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,711,'SuccessfulCashOutFromATM',1,'You have succesfully cashed out amount $(Currency) $(Amount) from ATM.',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,711,'SuccessfulCashOutFromATM',2,'You have succesfully cashed out amount $(Currency) $(Amount) from ATM.',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,711,'SuccessfulCashOutFromATM',4,'You have succesfully cashed out amount $(Currency) $(Amount) from ATM.',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,711,'SuccessfulCashOutFromATM',8,'You have succesfully cashed out amount $(Currency) $(Amount) from ATM.',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,711,'SuccessfulCashOutFromATM',16,'You have succesfully cashed out amount $(Currency) $(Amount) from ATM.',null,0,0,sysdate,null,null,1);

Delete from notification where code = 712;
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,712,'FailedCashOutFromATM',1,'Cash out for amount $(Currency) $(Amount) from ATM has been failed.',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,712,'FailedCashOutFromATM',2,'Cash out for amount $(Currency) $(Amount) from ATM has been failed.',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,712,'FailedCashOutFromATM',4,'Cash out for amount $(Currency) $(Amount) from ATM has been failed.',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,712,'FailedCashOutFromATM',8,'Cash out for amount $(Currency) $(Amount) from ATM has been failed.',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,712,'FailedCashOutFromATM',16,'Cash out for amount $(Currency) $(Amount) from ATM has been failed.',null,0,0,sysdate,null,null,1);

update channel_code set channelname='ATM', description='ATM' where id = 9 and channelcode = 9;

commit;