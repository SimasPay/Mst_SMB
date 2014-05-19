

ALTER TABLE `integration_partner_map` ADD IntegrationName VARCHAR(255) AFTER InstitutionID;

ALTER TABLE `integration_partner_map` ADD AuthenticationKey VARCHAR(255);

ALTER TABLE `integration_partner_map` ADD IsAuthenticationKeyEnabled tinyint(4) DEFAULT '0';

ALTER TABLE `integration_partner_map` ADD `IsLoginEnabled` tinyint(4) DEFAULT '0';

DROP TABLE IF EXISTS `ip_mapping`;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ip_mapping` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `IntegrationID` bigint(20) NOT NULL,
  `IPAddress` varchar(255) NOT NULL,
  PRIMARY KEY (`ID`),
  constraint FK_ip_map_Integration_ID foreign key (IntegrationID) references integration_partner_map (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


delete from permission_item where Permission in (19101, 19102, 19103, 19104, 19105);
delete from role_permission where Permission in (19101, 19102, 19103, 19104, 19105);
INSERT INTO `permission_item` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', NOW(), 'system', NOW(), 'system', 19101,1,'integrations','default','default');

INSERT INTO `role_permission` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', NOW(), 'system', NOW(), 'system', '1','19101');

INSERT INTO `permission_item` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', NOW(), 'system', NOW(), 'system', 19102,1,'integrations.view','default','default');

INSERT INTO `role_permission` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', NOW(), 'system', NOW(), 'system', '1','19102');

INSERT INTO `permission_item` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', NOW(), 'system', NOW(), 'system', 19103,1,'integrations.add','default','default');

INSERT INTO `role_permission` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', NOW(), 'system', NOW(), 'system', '1','19103');

INSERT INTO `permission_item` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', NOW(), 'system', NOW(), 'system', 19104,1,'integrations.edit','default','default');

INSERT INTO `role_permission` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', NOW(), 'system', NOW(), 'system', '1','19104');

INSERT INTO `permission_item` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', NOW(), 'system', NOW(), 'system', 19105,1,'integrations.delete','default','default');

INSERT INTO `role_permission` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', NOW(), 'system', NOW(), 'system', '1','19105');



delete from notification where code=751;

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,751,"AuthenticationKeyForIntegration",1,"Authentication Key for Integration $(IntegrationName) is $(AuthenticationKey)",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,751,"AuthenticationKeyForIntegration",2,"Authentication Key for Integration $(IntegrationName) is $(AuthenticationKey)",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,751,"AuthenticationKeyForIntegration",4,"Authentication Key for Integration $(IntegrationName) is $(AuthenticationKey)",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,751,"AuthenticationKeyForIntegration",8,"Authentication Key for Integration $(IntegrationName) is $(AuthenticationKey)",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,751,"AuthenticationKeyForIntegration",16,"Authentication Key for Integration $(IntegrationName) is $(AuthenticationKey)",null,0,0,now(),null,null,1);

delete from notification where code=752;

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,752,"ResetAuthenticationKeyForIntegration",1,"Authentication Key for Integration $(IntegrationName) is reset. New Authentication key is $(AuthenticationKey)",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,752,"ResetAuthenticationKeyForIntegration",2,"Authentication Key for Integration $(IntegrationName) is reset. New Authentication key is $(AuthenticationKey)",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,752,"ResetAuthenticationKeyForIntegration",4,"Authentication Key for Integration $(IntegrationName) is reset. New Authentication key is $(AuthenticationKey)",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,752,"ResetAuthenticationKeyForIntegration",8,"Authentication Key for Integration $(IntegrationName) is reset. New Authentication key is $(AuthenticationKey)",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,752,"ResetAuthenticationKeyForIntegration",16,"Authentication Key for Integration $(IntegrationName) is reset. New Authentication key is $(AuthenticationKey)",null,0,0,now(),null,null,1);
