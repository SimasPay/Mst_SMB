DELETE FROM service_transaction where serviceid=(select id from service where servicename='AgentServices') and transactiontypeid=(select id from transaction_type where transactionname='StarTimesPayment');
DELETE FROM service_transaction where serviceid=(select id from service where servicename='Payment') and transactiontypeid=(select id from transaction_type where transactionname='StarTimesPayment');
DELETE FROM rule_key where serviceid=(select id from service where ServiceName = 'Payment') and transactiontypeid=(select id from transaction_type where TransactionName = 'StarTimesPayment');
DELETE FROM rule_key where serviceid=(select id from service where ServiceName = 'AgentServices') and transactiontypeid=(select id from transaction_type where TransactionName = 'StarTimesPayment');
DELETE FROM transaction_type where TRANSACTIONNAME = 'StarTimesPayment';
DELETE FROM service_transaction where serviceid=(select id from service where servicename='Payment') and transactiontypeid=(select id from transaction_type where transactionname='StarTimesQueryBalance');
DELETE FROM transaction_type where TRANSACTIONNAME = 'StarTimesQueryBalance';
INSERT INTO transaction_type(VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME,CREATEDBY,MSPID,TRANSACTIONNAME,DISPLAYNAME) VALUES (1,now(),'System',now(),'System',1,'StarTimesPayment','Startimes Payment');

INSERT INTO service_transaction(VERSION,LASTUPDATETIME,UPDATEDBY,CREATETIME,CREATEDBY,MSPID,SERVICEID,TRANSACTIONTYPEID) VALUES (1,now(),'System',now(),'System',1, (select id from service where SERVICENAME = 'Payment'), (select id from transaction_type where TRANSACTIONNAME = 'StarTimesPayment'));

INSERT INTO transaction_type(VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME,CREATEDBY,MSPID,TRANSACTIONNAME,DISPLAYNAME) VALUES (1,now(),'System',now(),'System',1,'StarTimesQueryBalance','Startimes QueryBalance');

INSERT INTO service_transaction(VERSION,LASTUPDATETIME,UPDATEDBY,CREATETIME,CREATEDBY,MSPID,SERVICEID,TRANSACTIONTYPEID) VALUES (1,now(),'System',now(),'System',1, (select id from service where SERVICENAME = 'Payment'), (select id from transaction_type where TRANSACTIONNAME = 'StarTimesQueryBalance'));

INSERT INTO service_transaction(VERSION,LASTUPDATETIME,UPDATEDBY,CREATETIME,CREATEDBY,MSPID,SERVICEID,TRANSACTIONTYPEID) VALUES (1,now(),'System',now(),'System',1, (select id from service where SERVICENAME = 'AgentServices'), (select id from transaction_type where TRANSACTIONNAME = 'StarTimesPayment'));


Delete from system_parameters where ParameterName = 'startimes.biller.code';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,now(),'System',now(),'system','startimes.biller.code','STARTIMES','Startimes Biller Code');


DELETE FROM notification WHERE Code=2095;
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,2095,'QueryBalanceSuccessful',1,'Dear Subscriber your Balance amount is $(Amount)',null,0,0,now(),null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,2095,'QueryBalanceSuccessful',2,'Dear Subscriber your Balance amount is $(Amount)',null,0,0,now(),null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,2095,'QueryBalanceSuccessful',4,'Dear Subscriber your Balance amount is $(Amount)',null,0,0,now(),null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,2095,'QueryBalanceSuccessful',8,'Dear Subscriber your Balance amount is $(Amount)',null,0,0,now(),null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,2095,'QueryBalanceSuccessful',16,'Dear Subscriber your Balance amount is $(Amount)',null,0,0,now(),null,null,1);

DELETE FROM rule_key where serviceid=(select id from service where ServiceName = 'Payment') and transactiontypeid=(select id from transaction_type where TransactionName = 'StarTimesPayment');

INSERT INTO rule_key (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ServiceID, TransactionTypeID, TxnRuleKey, TxnRuleKeyType, TxnRuleKeyPriority,  TxnRuleKeyComparision) VALUES('1', now(), 'system', now(), 'system', (select id from service where ServiceName = 'Payment'), (select id from transaction_type where TransactionName = 'StarTimesPayment'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ServiceID, TransactionTypeID, TxnRuleKey, TxnRuleKeyType, TxnRuleKeyPriority,  TxnRuleKeyComparision) VALUES('1', now(), 'system', now(), 'system', (select id from service where ServiceName = 'Payment'), (select id from transaction_type where TransactionName = 'StarTimesPayment'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ServiceID, TransactionTypeID, TxnRuleKey, TxnRuleKeyType, TxnRuleKeyPriority,  TxnRuleKeyComparision) VALUES('1', now(), 'system', now(), 'system', (select id from service where ServiceName = 'Payment'), (select id from transaction_type where TransactionName = 'StarTimesPayment'), 'Channel', 'Standard', 10, 'Equal');

DELETE FROM rule_key where serviceid=(select id from service where ServiceName = 'AgentServices') and transactiontypeid=(select id from transaction_type where TransactionName = 'StarTimesPayment');

INSERT INTO rule_key (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ServiceID, TransactionTypeID, TxnRuleKey, TxnRuleKeyType, TxnRuleKeyPriority,  TxnRuleKeyComparision) VALUES('1', now(), 'system', now(), 'system', (select id from service where ServiceName = 'AgentServices'), (select id from transaction_type where TransactionName = 'StarTimesPayment'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ServiceID, TransactionTypeID, TxnRuleKey, TxnRuleKeyType, TxnRuleKeyPriority,  TxnRuleKeyComparision) VALUES('1', now(), 'system', now(), 'system', (select id from service where ServiceName = 'AgentServices'), (select id from transaction_type where TransactionName = 'StarTimesPayment'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ServiceID, TransactionTypeID, TxnRuleKey, TxnRuleKeyType, TxnRuleKeyPriority,  TxnRuleKeyComparision) VALUES('1', now(), 'system', now(), 'system', (select id from service where ServiceName = 'AgentServices'), (select id from transaction_type where TransactionName = 'StarTimesPayment'), 'Channel', 'Standard', 10, 'Equal');
