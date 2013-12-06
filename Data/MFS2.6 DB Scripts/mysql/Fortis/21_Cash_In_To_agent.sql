
-- Create Funding Pocket For  Agent for Service Partner
Delete from pocket where CARDPAN = 'wl1Hb4D+Yojm6MOu7FCeUQ==';
INSERT INTO pocket(VERSION,LASTUPDATETIME,UPDATEDBY,CREATETIME,CREATEDBY, POCKETTEMPLATEID, MDNID, LASTTRANSACTIONTIME, CURRENTBALANCE, CURRENTDAILYEXPENDITURE, CURRENTWEEKLYEXPENDITURE, CURRENTMONTHLYEXPENDITURE, CURRENTDAILYTXNSCOUNT, CURRENTWEEKLYTXNSCOUNT, CURRENTMONTHLYTXNSCOUNT, LASTBANKRESPONSECODE, LASTBANKAUTHORIZATIONCODE, LASTBANKREQUESTCODE, CARDPAN, RESTRICTIONS, ISDEFAULT, STATUS, STATUSTIME, ACTIVATIONTIME, OLDPOCKETTEMPLATEID, POCKETTEMPLATECHANGETIME, POCKETTEMPLATECHANGEDBY, LOWBALNOTIFTYPE, LOWBALNOTIFTRIGGERTIME, LOWBALNOTIFREGISTERED, LOWBALNOTIFQUERYTIME, COMPANYID) VALUES (1,now(),'System',now(),'System',5,(select id from subscriber_mdn where subscriberid = (select subscriberid from partner where businesspartnertype=0)),NULL,'/YST2/P0lVQ=',0.0000,0.0000,0.0000,0,0,0,NULL,NULL,NULL,'wl1Hb4D+Yojm6MOu7FCeUQ==',0,1,1,now(),now(),NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);

-- Define the Funding Pocket For  Agent system Parameter
Delete from system_parameters where ParameterName = 'funding.pocket.for.agent';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,now(),'System',now(),'system','funding.pocket.for.agent',(select max(id) from pocket),'Funding Pocket For Agent');


-- Creating cash in to agent trxn type
DELETE FROM service_transaction where serviceid=(select id from service where servicename='Wallet') and transactiontypeid=(select id from transaction_type where transactionname='CashInToAgent');
DELETE FROM transaction_type where TRANSACTIONNAME = 'CashInToAgent';
INSERT INTO transaction_type(VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME,CREATEDBY,MSPID,TRANSACTIONNAME,DISPLAYNAME) VALUES (1,now(),'System',now(),'System',1,'CashInToAgent','Cash In To Agent');
INSERT INTO service_transaction(VERSION,LASTUPDATETIME,UPDATEDBY,CREATETIME,CREATEDBY,MSPID,SERVICEID,TRANSACTIONTYPEID)  VALUES (1,now(),'System',now(),'System',1, (select id from service where SERVICENAME = 'Wallet'), (select id from transaction_type where TRANSACTIONNAME = 'CashInToAgent'));

DELETE FROM enum_text where TAGID=5636 and ENUMCODE='56';
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',now(),'system',now(),'system','0','TransactionUICategory','5636','56','Cash_In_To_Agent','Cash In To Agent');

