delete from offline_report where Name='AirtimeReport';
INSERT INTO `offline_report` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Name`,`ReportSql`,`ReportClass`,`TriggerEnable`) VALUES
 (1,now(),'system',now(),'system','AirtimeReport',NULL,'com.mfino.report.generalreports.AirtimeReport',1);
 
delete from offline_report where Name='AirtimeSummaryReport'; 
INSERT INTO `offline_report` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Name`,`ReportSql`,`ReportClass`,`TriggerEnable`) VALUES
 (1,now(),'system',now(),'system','AirtimeSummaryReport',NULL,'com.mfino.report.generalreports.AirtimeSummaryReport',1);


delete from offline_report where Name='MoneyTransferReport'; 
INSERT INTO `offline_report` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Name`,`ReportSql`,`ReportClass`,`TriggerEnable`) VALUES
 (1,now(),'system',now(),'system','MoneyTransferReport',NULL,'com.mfino.report.generalreports.MoneyTransferReport',1);

