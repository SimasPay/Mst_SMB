use mfino;
ALTER TABLE `credit_card_transaction` ADD INDEX `Index_Dest_MDN` USING BTREE(`MDN`, `CompanyID`);

ALTER TABLE `credit_card_transaction` ADD INDEX `Index_Auth_ID` USING BTREE(`AuthID`, `CompanyID`);

ALTER TABLE `credit_card_transaction` ADD INDEX `Index_Transaction_ID` USING BTREE(`TransactionID`, `CompanyID`);

ALTER TABLE `credit_card_transaction` ADD INDEX `Index_Bank_Ref_Num` USING BTREE(`BankReference`, `CompanyID`);

ALTER TABLE `credit_card_transaction` ADD INDEX `Index_Create_Time` USING BTREE(`CreateTime`, `CompanyID`);

INSERT INTO `offline_report` (`ID`, `Version`, `LastUpdateTime`, `UpdatedBy`, `CreateTime`, `CreatedBy`, `Name`, `ReportSql`, `ReportClass`) values('20','1',NOW(),'system',NOW(),'system','CreditCardTransaction',NULL,'com.mfino.report.CreditCardTransactionReport');
insert into    offline_report_company (`Version`, `LastUpdateTime`, `UpdatedBy`, `CreateTime`, `CreatedBy`, `reportid`,`companyid`) values ('1',NOW(),'system',NOW(),'system',20,1);
insert into    offline_report_company (`Version`, `LastUpdateTime`, `UpdatedBy`, `CreateTime`, `CreatedBy`, `reportid`,`companyid`) values ('1',NOW(),'system',NOW(),'system',20,2);
