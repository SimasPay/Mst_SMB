use mfino;

DELETE FROM `offline_report_receiver`;
DELETE FROM `offline_report`;

INSERT INTO `offline_report` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Name`,`ReportSql`,`ReportClass`) VALUES
 (1,now(),'system',now(),'system','AgentSalesCommisionReport',NULL,'com.mfino.report.zenithreport.AgentSalesCommisionReport'),
 (1,now(),'system',now(),'system','SubscriberClassificationReport',NULL,'com.mfino.report.zenithreport.SubscriberClassificationReport'),
 (1,now(),'system',now(),'system','SummaryReport',NULL,'com.mfino.report.zenithreport.SummaryReport'),
 (1,now(),'system',now(),'system','ConsolidatedSalesReport',NULL,'com.mfino.report.zenithreport.ConsolidatedSalesReport'),
 (1,now(),'system',now(),'system','CustomerRegistrationReport',NULL,'com.mfino.report.zenithreport.CustomerRegistrationReport'),
 (1,now(),'system',now(),'system','FundMovementReport',NULL,'com.mfino.report.zenithreport.FundMovementReport'),
 (1,now(),'system',now(),'system','MoneyAvailableReport',NULL,'com.mfino.report.zenithreport.MoneyAvailableReport'),
 (1,now(),'system',now(),'system','PendingTransactionReport',NULL,'com.mfino.report.zenithreport.PendingTransactionReport'),
 (1,now(),'system',now(),'system','ServiceChargeReport',NULL,'com.mfino.report.zenithreport.ServiceChargeReport'),
 (1,now(),'system',now(),'system','TransactionReport',NULL,'com.mfino.report.zenithreport.TransactionReport'),
 (1,now(),'system',now(),'system','PartnerTransactionReport',NULL,'com.mfino.report.zenithreport.PartnerTransactionReport'),
 (1,now(),'system',now(),'system','AggregatevalueAndTransactionsperSubscriberReport',NULL,'com.mfino.report.zenithreport.AggregatevalueAndTransactionsperSubscriberReport'),
 (1,now(),'system',now(),'system','MobileMoneyFailedTransactionReport',NULL,'com.mfino.report.zenithreport.MobileMoneyFailedTransactionReport'),
 (1,now(),'system',now(),'system','MobileMoneyRepeatedTransactionsReport',NULL,'com.mfino.report.zenithreport.MobileMoneyRepeatedTransactionsReport'),
 (1,now(),'system',now(),'system','ResolvedTransactionReport',NULL,'com.mfino.report.zenithreport.ResolvedTransactionReport'),
 (1,now(),'system',now(),'system','UserRolesAndRightsReport',NULL,'com.mfino.report.zenithreport.UserRolesAndRightsReport'),
 (1,now(),'system',now(),'system','RepeatedTransactionsPerTransactionTypeReport',NULL,'com.mfino.report.zenithreport.amlreports.RepeatedTransactionsPerTransactionTypeReport'),
 (1,now(),'system',now(),'system','OverLimitTransactionReport',NULL,'com.mfino.report.zenithreport.amlreports.OverLimitTransactionReport'),
 (1,now(),'system',now(),'system','ZMMAccountDeactiveReport',NULL,'com.mfino.report.zenithreport.amlreports.ZMMAccountDeactiveReport'),
 (1,now(),'system',now(),'system','AccountProfileChangeReport',NULL,'com.mfino.report.zenithreport.amlreports.AccountProfileChangeReport'),
 (1,now(),'system',now(),'system','UpdatedAccountsReport',NULL,'com.mfino.report.zenithreport.amlreports.UpdatedAccountsReport'),
 (1,now(),'system',now(),'system','DailyLimitUtilizationReport',NULL,'com.mfino.report.zenithreport.amlreports.DailyLimitUtilizationReport'),
 (1,now(),'system',now(),'system','KinInformationMissingAccountReport',NULL,'com.mfino.report.zenithreport.amlreports.KinInformationMissingAccountReport'),
 (1,now(),'system',now(),'system','B2E2BTransactionReport',NULL,'com.mfino.report.zenithreport.amlreports.B2E2BTransactionReport');


-- replace emailid
 INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='AgentSalesCommisionReport');
 INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='SubscriberClassificationReport');
 INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='SummaryReport');
 INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='ConsolidatedSalesReport');
 INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='CustomerRegistrationReport');
 INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='FundMovementReport');
 INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='MoneyAvailableReport');
 INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='PendingTransactionReport');
 INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='ServiceChargeReport');
 INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='TransactionReport');
 INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='PartnerTransactionReport');
  INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='AggregatevalueAndTransactionsperSubscriberReport');
 INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='MobileMoneyFailedTransactionReport');
 INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='MobileMoneyRepeatedTransactionsReport');
 INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='ResolvedTransactionReport');
 INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='UserRolesAndRightsReport');
 INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='RepeatedTransactionsPerTransactionTypeReport');
  INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='OverLimitTransactionReport');
 INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='ZMMAccountDeactiveReport');
 INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='AccountProfileChangeReport');
 INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='UpdatedAccountsReport');
INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='DailyLimitUtilizationReport');
INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='KinInformationMissingAccountReport');
INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='B2E2BTransactionReport');



DROP TABLE IF EXISTS `report_parameters`;
CREATE TABLE  `report_parameters` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `ParameterName` varchar(255) DEFAULT NULL,
  `ParameterValue` varchar(255) DEFAULT NULL,
  `Description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;  

 INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','report.limit.cashin','2','cashin transaction limit '),
 (1,now(),'system',now(),'system','report.limit.cashout','2','cashout transaction limit '),
 (1,now(),'system',now(),'system','report.limit.p2p','2','p2p transaction limit '),
 (1,now(),'system',now(),'system','report.limit.billpay','2','billpay transaction limit '),
 (1,now(),'system',now(),'system','report.limit.bank2emoney','2','b2e transaction limit '),
 (1,now(),'system',now(),'system','report.limit.bank2bank','2','b2b transaction limit '),
 (1,now(),'system',now(),'system','report.limit.emoney2bank','2','e2b transaction limit'),
 (1,now(),'system',now(),'system','report.limit.emoney2bank2emoney','2','e2b2e transaction limit'),
 (1,now(),'system',now(),'system','report.limit.dailyutilizationpercent','30','daily limit utilization in percentage'),
 (1,now(),'system',now(),'system','report.account.deactivatetime','30','days with out any transaction to deactivate account'),
 (1,now(),'system',now(),'system','report.successtransactionamount.limit','100','successful transaction amount limit to report'),
 (1,now(),'system',now(),'system','report.failedtransactions.limit','2','mobile money failed transaction count to report'),
 (1,now(),'system',now(),'system','report.duplicatetransaction.limit','2','mobile money failed');