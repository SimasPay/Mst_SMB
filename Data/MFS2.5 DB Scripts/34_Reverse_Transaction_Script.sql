use mfino;
-- Alter the Service Charge Transaction Log table
ALTER TABLE `service_charge_transaction_log` ADD COLUMN `IsChargeDistributed` TINYINT(4) DEFAULT 0;
update service_charge_transaction_log set IsChargeDistributed = 1 where status = 4;

ALTER TABLE `service_charge_transaction_log` ADD COLUMN `ParentSCTLID` BIGINT(20), ADD COLUMN `ReversalReason` VARCHAR(255);

ALTER TABLE `service_charge_transaction_log` ADD COLUMN `IsTransactionReversed` TINYINT(4) DEFAULT 0;

-- Creates the new transaction type
INSERT Ignore INTO `transaction_type` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`TransactionName`,`DisplayName`) VALUES
(1,now(),'system',now(),'system',1,'ReverseTransaction','Reverse Transaction');

 INSERT ignore INTO `service_transaction` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`ServiceID`, `TransactionTypeID`) VALUES
(1,now(),'system',now(),'system',1,(select id from service where ServiceName='Wallet'),(select id from transaction_type where TransactionName='ReverseTransaction'));

-- Inserts the new status values for service charge transaction log table
INSERT IGNORE INTO `enum_text` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',NOW(),'system',NOW(),'system','0','SCTLStatus','6089','7','Reverse_Requested','Reverse Requested');
INSERT IGNORE INTO `enum_text` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',NOW(),'system',NOW(),'system','0','SCTLStatus','6089','8','Reverse_Initiated','Reverse Initiated');
INSERT IGNORE INTO `enum_text` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',NOW(),'system',NOW(),'system','0','SCTLStatus','6089','9','Reverse_Approved','Reverse Approved');
INSERT IGNORE INTO `enum_text` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',NOW(),'system',NOW(),'system','0','SCTLStatus','6089','10','Reverse_Rejected','Reverse Rejected');
INSERT IGNORE INTO `enum_text` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',NOW(),'system',NOW(),'system','0','SCTLStatus','6089','11','Reverse_Start','Reverse Start');
INSERT IGNORE INTO `enum_text` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',NOW(),'system',NOW(),'system','0','SCTLStatus','6089','12','Reverse_Processing','Reverse Processing');
INSERT IGNORE INTO `enum_text` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',NOW(),'system',NOW(),'system','0','SCTLStatus','6089','13','Reverse_Success','Reverse Success');
INSERT IGNORE INTO `enum_text` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',NOW(),'system',NOW(),'system','0','SCTLStatus','6089','14','Reversed','Reversed');
INSERT IGNORE INTO `enum_text` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',NOW(),'system',NOW(),'system','0','SCTLStatus','6089','15','Reverse_Failed','Reverse Failed');

update enum_text set DisplayText = 'Distribution Started' where TagID = '6089' and EnumCode = '3';
update enum_text set DisplayText = 'Distribution Completed' where TagID = '6089' and EnumCode = '4';
update enum_text set DisplayText = 'Distribution Failed' where TagID = '6089' and EnumCode = '6';

INSERT IGNORE INTO `mfino`.`enum_text` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',NOW(),'system',NOW(),'system','0','TransactionUICategory','5636','40','Reverse_Transaction','Reverse Transaction');

-- Inserts the new Permission items
INSERT INTO `permission_item` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', NOW(), 'system', NOW(), 'system', 13502,1,'chargeTransaction.reverse','default','default');
INSERT INTO `permission_item` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', NOW(), 'system', NOW(), 'system', 13503,1,'chargeTransaction.approve','default','default');

INSERT INTO `role_permission` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', NOW(), 'system', NOW(), 'system', '1','13502');
INSERT INTO `role_permission` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', NOW(), 'system', NOW(), 'system', '25','13503');

-- Inserts the new notification messages
INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,666,"ReverseTransactionCompleteToSender",1,"Transaction ID: $(TransferID). Dear Customer, you have been debited of $(Currency) $(Amount) as Reversal of Transaction $(OriginalTransferID).",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,666,"ReverseTransactionCompleteToSender",2,"Transaction ID: $(TransferID). Dear Customer, you have been debited of $(Currency) $(Amount) as Reversal of Transaction $(OriginalTransferID).",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,666,"ReverseTransactionCompleteToSender",4,"Transaction ID: $(TransferID). Dear Customer, you have been debited of $(Currency) $(Amount) as Reversal of Transaction $(OriginalTransferID).",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,666,"ReverseTransactionCompleteToSender",8,"Transaction ID: $(TransferID). Dear Customer, you have been debited of $(Currency) $(Amount) as Reversal of Transaction $(OriginalTransferID).",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,666,"ReverseTransactionCompleteToSender",16,"Transaction ID: $(TransferID). Dear Customer, you have been debited of $(Currency) $(Amount) as Reversal of Transaction $(OriginalTransferID).",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,667,"ReverseTransactionCompleteToReceiver",1,"Transaction ID: $(TransferID). Dear Customer, you have been credited of $(Currency) $(Amount) as Reversal of Transaction $(OriginalTransferID).",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,667,"ReverseTransactionCompleteToReceiver",2,"Transaction ID: $(TransferID). Dear Customer, you have been credited of $(Currency) $(Amount) as Reversal of Transaction $(OriginalTransferID).",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,667,"ReverseTransactionCompleteToReceiver",4,"Transaction ID: $(TransferID). Dear Customer, you have been credited of $(Currency) $(Amount) as Reversal of Transaction $(OriginalTransferID).",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,667,"ReverseTransactionCompleteToReceiver",8,"Transaction ID: $(TransferID). Dear Customer, you have been credited of $(Currency) $(Amount) as Reversal of Transaction $(OriginalTransferID).",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,667,"ReverseTransactionCompleteToReceiver",16,"Transaction ID: $(TransferID). Dear Customer, you have been credited of $(Currency) $(Amount) as Reversal of Transaction $(OriginalTransferID).",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,668,"ReverseTransactionRequestInitiated",1,"Dear Customer, Reversal of Transaction $(OriginalTransferID) is initiated.",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,668,"ReverseTransactionRequestInitiated",2,"Dear Customer, Reversal of Transaction $(OriginalTransferID) is initiated.",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,668,"ReverseTransactionRequestInitiated",4,"Dear Customer, Reversal of Transaction $(OriginalTransferID) is initiated.",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,668,"ReverseTransactionRequestInitiated",8,"Dear Customer, Reversal of Transaction $(OriginalTransferID) is initiated.",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,668,"ReverseTransactionRequestInitiated",16,"Dear Customer, Reversal of Transaction $(OriginalTransferID) is initiated.",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,669,"ReverseTransactionRequestRejected",1,"Dear Customer, Reversal of Transaction $(OriginalTransferID) is Rejected.",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,669,"ReverseTransactionRequestRejected",2,"Dear Customer, Reversal of Transaction $(OriginalTransferID) is Rejected.",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,669,"ReverseTransactionRequestRejected",4,"Dear Customer, Reversal of Transaction $(OriginalTransferID) is Rejected.",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,669,"ReverseTransactionRequestRejected",8,"Dear Customer, Reversal of Transaction $(OriginalTransferID) is Rejected.",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,669,"ReverseTransactionRequestRejected",16,"Dear Customer, Reversal of Transaction $(OriginalTransferID) is Rejected.",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,670,"ReverseTransactionRequestFailed",1,"Dear Customer, Reversal of Transaction $(OriginalTransferID) is Failed.",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,670,"ReverseTransactionRequestFailed",2,"Dear Customer, Reversal of Transaction $(OriginalTransferID) is Failed.",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,670,"ReverseTransactionRequestFailed",4,"Dear Customer, Reversal of Transaction $(OriginalTransferID) is Failed.",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,670,"ReverseTransactionRequestFailed",8,"Dear Customer, Reversal of Transaction $(OriginalTransferID) is Failed.",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,670,"ReverseTransactionRequestFailed",16,"Dear Customer, Reversal of Transaction $(OriginalTransferID) is Failed.",null,0,0,now(),null,null,1);
