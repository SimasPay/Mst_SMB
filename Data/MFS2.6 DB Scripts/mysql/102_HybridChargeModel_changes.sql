
--
-- Add new columns Charge, MinCharge & MaxCharge to charge_pricing table
--
Alter table charge_pricing add column Charge varchar(255) DEFAULT NULL;
Alter table charge_pricing add column MinCharge varchar(255) DEFAULT NULL;
Alter table charge_pricing add column MaxCharge varchar(255) DEFAULT NULL;

--
-- Add new columns ActualSharePercentage, MinSharePercentage & MaxSharePercentage to share_partner table
--
Alter table share_partner add column ActualSharePercentage varchar(255) DEFAULT NULL; 
Alter table share_partner add column MinSharePercentage varchar(255) DEFAULT NULL;
Alter table share_partner add column MaxSharePercentage varchar(255) DEFAULT NULL;

--
-- Create new notification 810 : InvalidChargeDefinitionException
--
Delete from notification where code = 810;
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,810,'InvalidChargeDefinitionException',1,'Transaction failed due to Invalid charge definition.',null,0,0,now(),null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,810,'InvalidChargeDefinitionException',2,'Transaction failed due to Invalid charge definition.',null,0,0,now(),null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,810,'InvalidChargeDefinitionException',4,'Transaction failed due to Invalid charge definition.',null,0,0,now(),null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,810,'InvalidChargeDefinitionException',8,'Transaction failed due to Invalid charge definition.',null,0,0,now(),null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,810,'InvalidChargeDefinitionException',16,'Transaction failed due to Invalid charge definition.',null,0,0,now(),null,null,1);
