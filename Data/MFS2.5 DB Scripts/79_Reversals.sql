use mfino;

ALTER TABLE `mfino`.`service_charge_txn_log` MODIFY COLUMN `Status` INT(11) UNSIGNED NOT NULL,
 ADD COLUMN `AmtRevStatus` INT(11) AFTER `IsTransactionReversed`,
 ADD COLUMN `ChrgRevStatus` INT(11) AFTER `AmtRevStatus`;

INSERT Ignore INTO `transaction_type` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`TransactionName`,`DisplayName`) VALUES (1,now(),'system',now(),'system',1,'ReverseCharge','Reverse Charge');

INSERT ignore INTO `service_transaction` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`ServiceID`, `TransactionTypeID`) VALUES (1,now(),'system',now(),'system',1,(select id from service where ServiceName='Wallet'),(select id from transaction_type where TransactionName='ReverseCharge'));

INSERT IGNORE INTO `mfino`.`enum_text` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',NOW(),'system',NOW(),'system','0','TransactionUICategory','5636','48','Reverse_Charge','Reverse Charge');

insert into system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,now(),'System',now(),'system','charge.reversal.funding.pocket','','Pocket id for charge reversal funding');
