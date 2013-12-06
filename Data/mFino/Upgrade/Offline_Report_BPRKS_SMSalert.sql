use mfino;
INSERT INTO `offline_report` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Name`,`ReportSql`,`ReportClass`) VALUES
 (1,now(),'system',now(),'system','BankBPRKSFailed',NULL,'com.mfino.report.BankBPRKSFailedReport'),
 (1,now(),'system',now(),'system','BankBPRKS',NULL,'com.mfino.report.BankBPRKSReport'),
 (1,now(),'system',now(),'system','SMSAlert',NULL,'com.mfino.report.SMSAlertReport');


INSERT IGNORE INTO `mfino`.`offline_report_company` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, CompanyID)
 (select '1',NOW(),'system',NOW(),'system', id, 1 from offline_report where name ='BankBPRKSFailed');
 INSERT IGNORE INTO `mfino`.`offline_report_company` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, CompanyID)
 (select '1',NOW(),'system',NOW(),'system', id, 2 from offline_report where name ='BankBPRKSFailed');
 INSERT IGNORE INTO `mfino`.`offline_report_company` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, CompanyID)
 (select '1',NOW(),'system',NOW(),'system', id, 1 from offline_report where name ='BankBPRKS');
  INSERT IGNORE INTO `mfino`.`offline_report_company` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, CompanyID)
 (select '1',NOW(),'system',NOW(),'system', id, 2 from offline_report where name ='BankBPRKS');
  INSERT IGNORE INTO `mfino`.`offline_report_company` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, CompanyID)
 (select '1',NOW(),'system',NOW(),'system', id, 1 from offline_report where name ='SMSAlert');
 
 
 INSERT IGNORE INTO `mfino`.`offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name ='BankBPRKSFailed');
 INSERT IGNORE INTO `mfino`.`offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name ='BankBPRKS');
 INSERT IGNORE INTO `mfino`.`offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name ='SMSAlert');
 