
Delete FROM permission_item where Permission = '13504';
INSERT INTO `permission_item` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', NOW(), 'system', NOW(), 'system', 13504,1,'chargeTransaction.resendAccessCode','default','default');

Delete FROM role_permission where Permission = '13504' and Role = '1';

INSERT INTO `role_permission` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', NOW(), 'system', NOW(), 'system', '1','13504');

delete from notification where code=707;

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,707,"ResendAccessCodeNotificationToSenderOfUnregisteredTransfer",1,"Fund access code for the Transaction ID $(TransferID) is reset. You initiated a transaction of amount $(Currency) $(Amount) to $(ReceiverMDN). New Fund access code for the same is $(OneTimePin)",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,707,"ResendAccessCodeNotificationToSenderOfUnregisteredTransfer",2,"Fund access code for the Transaction ID $(TransferID) is reset. You initiated a transaction of amount $(Currency) $(Amount) to $(ReceiverMDN). New Fund access code for the same is $(OneTimePin)",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,707,"ResendAccessCodeNotificationToSenderOfUnregisteredTransfer",4,"Fund access code for the Transaction ID $(TransferID) is reset. You initiated a transaction of amount $(Currency) $(Amount) to $(ReceiverMDN). New Fund access code for the same is $(OneTimePin)",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,707,"ResendAccessCodeNotificationToSenderOfUnregisteredTransfer",8,"Fund access code for the Transaction ID $(TransferID) is reset. You initiated a transaction of amount $(Currency) $(Amount) to $(ReceiverMDN). New Fund access code for the same is $(OneTimePin)",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,707,"ResendAccessCodeNotificationToSenderOfUnregisteredTransfer",16,"Fund access code for the Transaction ID $(TransferID) is reset. You initiated a transaction of amount $(Currency) $(Amount) to $(ReceiverMDN). New Fund access code for the same is $(OneTimePin)",null,0,0,now(),null,null,1);

