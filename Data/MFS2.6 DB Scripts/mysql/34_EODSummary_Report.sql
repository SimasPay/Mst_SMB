

INSERT INTO `offline_report` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Name`,`ReportSql`,`ReportClass`,`TriggerEnable`) VALUES
 (1,now(),'system',now(),'system','EODSummaryReport',NULL,'com.mfino.report.generalreports.EODSummaryReport',0);


-- replace emailid
 INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='EODSummaryReport');
