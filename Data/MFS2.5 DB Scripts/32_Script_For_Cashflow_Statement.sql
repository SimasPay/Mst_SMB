use mfino;

 DELETE FROM  `report_parameters` Where `ParameterName`=  'report.financialyear.day';
  DELETE FROM  `report_parameters` Where `ParameterName`=  'report.financialyear.month';
 INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','report.financialyear.day','1','finacial year start day '),
 (1,now(),'system',now(),'system','report.financialyear.month','3','finacial year start Month');
 
 INSERT INTO `offline_report` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Name`,`ReportSql`,`ReportClass`) VALUES
 (1,now(),'system',now(),'system','MFSIncomeReport',NULL,'com.mfino.report.zenith.financial.MFSIncomeReport'),
 (1,now(),'system',now(),'system','CashFlowStatement',NULL,'com.mfino.report.zenith.financial.CashFlowStatement'),
 (1,now(),'system',now(),'system','MFSBalanceSheet',NULL,'com.mfino.report.zenith.financial.ACCSnapShot');
 
  INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='MFSIncomeReport');
 INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='CashFlowStatement');
 INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='MFSBalanceSheet');